package com.scolastico.discord_exe.config;

public class ConfigDataStore {

    private String mysql_server = "localhost:3306";
    private String mysql_user = "username";
    private String mysql_pass = "password";
    private String mysql_database = "database";
    private String mysql_prefix = "disc0rd_";
    private String discord_token = "token";

    public String getDiscordToken() {
        return discord_token;
    }

    public String getMysqlServer() {
        return mysql_server;
    }

    public String getMysqlDatabase() {
        return mysql_database;
    }

    public String getMysqlPass() {
        return mysql_pass;
    }

    public String getMysqlPrefix() {
        return mysql_prefix;
    }

    public String getMysqlUser() {
        return mysql_user;
    }

    public void setDiscordToken(String discord_token) {
        this.discord_token = discord_token;
    }

    public void setMysqlServer(String mysql_server) {
        this.mysql_server = mysql_server;
    }

    public void setMysqlDatabase(String mysql_database) {
        this.mysql_database = mysql_database;
    }

    public void setMysqlPass(String mysql_pass) {
        this.mysql_pass = mysql_pass;
    }

    public void setMysqlPrefix(String mysql_prefix) {
        this.mysql_prefix = mysql_prefix;
    }

    public void setMysqlUser(String mysql_user) {
        this.mysql_user = mysql_user;
    }
}
