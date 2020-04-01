package com.scolastico.discord_exe.config;

public class Pr0grammConfigDataStore {

    private String username = "Disc0rdExe";
    private String password = "password";
    private String token = "token";
    private OAuth oAuth = new OAuth();
    private int checkSchedule = 60;
    private int checkUsersAndClearSchedule = 60*60*3;
    private long lastPost = 0;

    public long getLastPost() {
        return lastPost;
    }

    public void setLastPost(long lastPost) {
        this.lastPost = lastPost;
    }

    public int getCheckSchedule() {
        return checkSchedule;
    }

    public void setCheckSchedule(int checkSchedule) {
        this.checkSchedule = checkSchedule;
    }

    public int getCheckUsersAndClearSchedule() {
        return checkUsersAndClearSchedule;
    }

    public void setCheckUsersAndClearSchedule(int checkUsersAndClearSchedule) {
        this.checkUsersAndClearSchedule = checkUsersAndClearSchedule;
    }

    public OAuth getOAuth() {
        return oAuth;
    }

    public void setOAuth(OAuth oAuth) {
        this.oAuth = oAuth;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class OAuth {
        private String clientId = "clientId";
        private String secret = "secret";
        private String url = "https://pr0gramm.com/auth/APPNAME/";
        private int keyValidUntil = 60*10;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getKeyValidUntil() {
            return keyValidUntil;
        }

        public void setKeyValidUntil(int keyValidUntil) {
            this.keyValidUntil = keyValidUntil;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }

}
