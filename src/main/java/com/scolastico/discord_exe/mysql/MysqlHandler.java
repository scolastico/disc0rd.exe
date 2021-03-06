package com.scolastico.discord_exe.mysql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scolastico.discord_exe.etc.ErrorHandler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class MysqlHandler {

    private String prefix;
    private Connection connection;
    private Gson gson = new GsonBuilder().create();

    public MysqlHandler(String server, String user, String pass, String db, String prefix) throws SQLException {

        this.prefix = prefix;

        connection = DriverManager.getConnection("jdbc:mysql://" + server + "/" + db + "?user=" + user + "&password=" + pass + "&serverTimezone=UTC&autoReconnect=true");
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "serverSettings` (`id` BIGINT NOT NULL, `json` text NOT NULL, PRIMARY KEY( `id` ));").execute();

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

    public HashMap<Long, ServerSettings> getAllServerSettingsWithId() {
        HashMap<Long, ServerSettings> serverSettings = new HashMap<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM `" + prefix + "serverSettings`;");
            while (rs.next()) {
                serverSettings.put(rs.getLong("id"), gson.fromJson(URLDecoder.decode(rs.getString("json"), StandardCharsets.UTF_8.toString()), ServerSettings.class));
            }
        } catch (SQLException | UnsupportedEncodingException e) {
            ErrorHandler.getInstance().handle(e);
        }
        return serverSettings;
    }

    public ServerSettings getServerSettings(long id) {
        String json = "{}";
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM `" + prefix + "serverSettings` WHERE `id`=" + id + ";");
            while (rs.next()) {
                json = URLDecoder.decode(rs.getString("json"), StandardCharsets.UTF_8.toString());
            }
        } catch (SQLException | UnsupportedEncodingException e) {
            ErrorHandler.getInstance().handle(e);
        }
        return gson.fromJson(json, ServerSettings.class);
    }

    public void setServerSettings(long id, ServerSettings settings) {
        try {
            String json = gson.toJson(settings);
            connection.prepareStatement("INSERT INTO `" + prefix + "serverSettings` (id, json) VALUES(" + id + ", '" + json + "') ON DUPLICATE KEY UPDATE json='" + URLEncoder.encode(json, StandardCharsets.UTF_8.toString()) + "';").execute();
        } catch (SQLException | UnsupportedEncodingException e) {
            ErrorHandler.getInstance().handle(e);
        }
    }

}
