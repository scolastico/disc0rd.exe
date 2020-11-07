package com.scolastico.discord_exe.mysql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;

import com.scolastico.discord_exe.etc.LockableObjectHolder;
import com.scolastico.discord_exe.etc.ScheduleTask;
import com.scolastico.discord_exe.etc.Tools;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class MysqlHandler {

    private final String prefix;
    private final Connection connection;
    private final Gson gson = new GsonBuilder().create();
    private final HashMap<Long, CacheEntry> cache = new HashMap<>();

    public MysqlHandler(String server, String user, String pass, String db, String prefix) throws SQLException {

        this.prefix = prefix;

        connection = DriverManager.getConnection("jdbc:mysql://" + server + "/" + db + "?user=" + user + "&password=" + pass + "&serverTimezone=UTC&autoReconnect=true");
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "serverSettings` (`id` BIGINT NOT NULL, `json` text NOT NULL, PRIMARY KEY( `id` ));").execute();

        ScheduleTask.getInstance().runScheduledTaskRepeat(new Runnable() {
            @Override
            public void run() {
                try {
                    deleteOldFromCache();
                } catch (Exception e) {
                    ErrorHandler.getInstance().handle(e);
                }
            }
        }, 15*20, 15*20, true);

    }

    private void deleteOldFromCache() {
        synchronized (this) {
            try {
                ArrayList<Long> toDelete = new ArrayList<>();
                for (Long id:cache.keySet()) {
                    CacheEntry entry = cache.get(id);
                    if ((Tools.getInstance().getUnixTimeStamp() - entry.getLastUse()) >= Disc0rd.getConfig().getMysqlCache()) {
                        toDelete.add(id);
                        setServerSettings(id, cache.get(id).getSettings());
                    }
                }
                for (Long id:toDelete) {
                    cache.remove(id);
                }
            } catch (Exception e) {
                ErrorHandler.getInstance().handle(e);
            }
        }
    }

    public HashMap<Long, ServerSettings> getAllServerSettings() {
        HashMap<Long, ServerSettings> serverSettings = new HashMap<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM `" + prefix + "serverSettings`;");
            while (rs.next()) {
                serverSettings.put(rs.getLong("id") ,gson.fromJson(URLDecoder.decode(rs.getString("json"), StandardCharsets.UTF_8.toString()), ServerSettings.class));
            }
        } catch (SQLException | UnsupportedEncodingException e) {
            ErrorHandler.getInstance().handle(e);
        }
        return serverSettings;
    }

    public ServerSettings getServerSettings(long id) {
        synchronized(this) {
            deleteOldFromCache();
            if (cache.containsKey(id)) {
                CacheEntry entry = cache.get(id);
                return entry.getSettings();
            }
            String json = "{}";
            try {
                ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM `" + prefix + "serverSettings` WHERE `id`=" + id + ";");
                while (rs.next()) {
                    json = URLDecoder.decode(rs.getString("json"), StandardCharsets.UTF_8.toString());
                }
            } catch (SQLException | UnsupportedEncodingException e) {
                ErrorHandler.getInstance().handle(e);
            }
            ServerSettings settings = gson.fromJson(json, ServerSettings.class);
            cache.put(id, new CacheEntry(settings));
            return settings;
        }
    }

    private void setServerSettings(long id, ServerSettings settings) {
        try {
            String json = gson.toJson(settings);
            connection.prepareStatement("INSERT INTO `" + prefix + "serverSettings` (id, json) VALUES(" + id + ", '" + json + "') ON DUPLICATE KEY UPDATE json='" + URLEncoder.encode(json, StandardCharsets.UTF_8.toString()) + "';").execute();
        } catch (SQLException | UnsupportedEncodingException e) {
            ErrorHandler.getInstance().handle(e);
        }
    }

    private static class CacheEntry {
        private Long lastUse = Tools.getInstance().getUnixTimeStamp();
        private final ServerSettings settings;

        public CacheEntry(ServerSettings settings) {
            this.settings = settings;
        }

        public Long getLastUse() {
            return lastUse;
        }

        public ServerSettings getSettings() {
            lastUse = Tools.getInstance().getUnixTimeStamp();
            return settings;
        }
    }

}
