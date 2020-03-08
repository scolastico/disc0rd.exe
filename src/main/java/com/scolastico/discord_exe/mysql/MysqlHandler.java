package com.scolastico.discord_exe.mysql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scolastico.discord_exe.etc.ErrorHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MysqlHandler {

    private String prefix;
    private Connection connection;
    private Gson gson = new GsonBuilder().create();

    public MysqlHandler(String server, String user, String pass, String db, String prefix) throws SQLException {

        this.prefix = prefix;

        connection = DriverManager.getConnection("jdbc:mysql://" + server + "/" + db + "?user=" + user + "&password=" + pass + "&serverTimezone=UTC&autoReconnect=true");
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "serverSettings` (`id` BIGINT NOT NULL, `json` text NOT NULL, PRIMARY KEY( `id` ));").execute();

    }

    public ArrayList<ServerSettings> getAllServerSettings() {
        ArrayList<ServerSettings> serverSettings = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM `" + prefix + "serverSettings`;");
            while (rs.next()) {
                serverSettings.add(gson.fromJson(rs.getString("json"), ServerSettings.class));
            }
        } catch (SQLException e) {
            ErrorHandler.getInstance().handle(e);
        }
        return serverSettings;
    }

    public ServerSettings getServerSettings(long id) {
        String json = "{}";
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM `" + prefix + "serverSettings` WHERE `id`=" + id + ";");
            while (rs.next()) {
                json = rs.getString("json");
            }
        } catch (SQLException e) {
            ErrorHandler.getInstance().handle(e);
        }
        return gson.fromJson(json, ServerSettings.class);
    }

    public void setServerSettings(long id, ServerSettings settings) {
        try {
            String json = gson.toJson(settings);
            connection.prepareStatement("INSERT INTO `" + prefix + "serverSettings` (id, json) VALUES(" + id + ", '" + json + "') ON DUPLICATE KEY UPDATE json='" + json + "';").execute();
        } catch (SQLException e) {
            ErrorHandler.getInstance().handle(e);
        }
    }

}
