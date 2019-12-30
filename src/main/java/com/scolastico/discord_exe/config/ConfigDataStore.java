package com.scolastico.discord_exe.config;

public class ConfigDataStore {

    private String mysql_server = "localhost:3306";
    private String mysql_user = "username";
    private String mysql_pass = "password";
    private String mysql_database = "database";
    private String mysql_prefix = "disc0rd_";
    private String discord_token = "token";

    public String getDiscord_token() {
        return discord_token;
    }

    public String getMysql_server() {
        return mysql_server;
    }

    public String getMysql_database() {
        return mysql_database;
    }

    public String getMysql_pass() {
        return mysql_pass;
    }

    public String getMysql_prefix() {
        return mysql_prefix;
    }

    public String getMysql_user() {
        return mysql_user;
    }

    public void setDiscord_token(String discord_token) {
        this.discord_token = discord_token;
    }

    public void setMysql_server(String mysql_server) {
        this.mysql_server = mysql_server;
    }

    public void setMysql_database(String mysql_database) {
        this.mysql_database = mysql_database;
    }

    public void setMysql_pass(String mysql_pass) {
        this.mysql_pass = mysql_pass;
    }

    public void setMysql_prefix(String mysql_prefix) {
        this.mysql_prefix = mysql_prefix;
    }

    public void setMysql_user(String mysql_user) {
        this.mysql_user = mysql_user;
    }
}
