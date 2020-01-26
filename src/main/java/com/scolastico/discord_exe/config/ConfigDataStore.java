package com.scolastico.discord_exe.config;

public class ConfigDataStore {

    private String discordToken = "token";
    private WebServerData webServer = new WebServerData();
    private MysqlData mysql = new MysqlData();
    private int maxErrorCountToShutDown = 10;

    public int getMaxErrorCountToShutDown() {
        return maxErrorCountToShutDown;
    }

    public void setMaxErrorCountToShutDown(int maxErrorCountToShutDown) {
        this.maxErrorCountToShutDown = maxErrorCountToShutDown;
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public MysqlData getMysql() {
        return mysql;
    }

    public WebServerData getWebServer() {
        return webServer;
    }

    public void setDiscordToken(String discordToken) {
        this.discordToken = discordToken;
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
        private String domain = "http://localhost:8040/";

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

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
