package com.scolastico.discord_exe.config;

public class ConfigDataStore {

    private String discord_token = "token";
    private WebServerData webServer = new WebServerData();
    private MysqlData mysql = new MysqlData();

    public String getDiscordToken() {
        return discord_token;
    }

    public MysqlData getMysql() {
        return mysql;
    }

    public WebServerData getWebServer() {
        return webServer;
    }

    public void setDiscordToken(String discord_token) {
        this.discord_token = discord_token;
    }

    public void setMysql(MysqlData mysql) {
        this.mysql = mysql;
    }

    public void setWebServer(WebServerData webServer) {
        this.webServer = webServer;
    }

    public static class WebServerData {
        private int port = 8040;
        private int buffer = 1024;

        public int getPort() {
            return port;
        }

        public int getBuffer() {
            return buffer;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public void setBuffer(int buffer) {
            this.buffer = buffer;
        }

        private WebServerData() {}
    }

    public static class MysqlData {
        private String server = "localhost:3306";
        private String user = "username";
        private String pass = "password";
        private String database = "database";
        private String prefix = "disc0rd_";

        public String getServer() {
            return server;
        }

        public String getDatabase() {
            return database;
        }

        public String getPass() {
            return pass;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getUser() {
            return user;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public void setUser(String user) {
            this.user = user;
        }

        private MysqlData() {}
    }

}
