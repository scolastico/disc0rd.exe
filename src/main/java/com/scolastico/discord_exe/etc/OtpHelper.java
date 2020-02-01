package com.scolastico.discord_exe.etc;

import com.scolastico.discord_exe.config.ConfigHandler;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public class OtpHelper {

    private GoogleAuthenticator gAuth = new GoogleAuthenticator();
    private OtpConfig otpConfig = new OtpConfig();
    private static OtpHelper instance = null;
    private int lastUsedPassword = 0;

    public static OtpHelper getInstance() {
        if (instance == null) {
            instance = new OtpHelper();
        }
        return instance;
    }

    private OtpHelper() {
        try {
            ConfigHandler configHandler;
            try {
                configHandler = new ConfigHandler(otpConfig, "otpConfig.json", true);
            } catch (Exception ignored) {
                GoogleAuthenticatorKey key = gAuth.createCredentials();
                otpConfig.setKey(key.getKey());
                configHandler = new ConfigHandler(otpConfig, "otpConfig.json");
                configHandler.setConfigObject(otpConfig);
                configHandler.saveConfigObject();
            }
            if (!(configHandler.getConfigObject() instanceof OtpConfig)) {
                ErrorHandler.getInstance().handleFatal(new Exception("Config Handler has not an OtpConfig object in it!"));
                return;
            }
            otpConfig = (OtpConfig) configHandler.getConfigObject();
            GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder gAuthConfBuilder = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().setWindowSize(otpConfig.getValidRange());
            gAuth = new GoogleAuthenticator(gAuthConfBuilder.build());
        } catch (Exception e) {
            ErrorHandler.getInstance().handleFatal(e);
        }
    }

    public int getPassword() {
        try {
            return gAuth.getTotpPassword(otpConfig.getKey());
        } catch (Exception e) {
            ErrorHandler.getInstance().handleFatal(e);
            return 0;
        }
    }

    public boolean isValid(int password) {
        if (lastUsedPassword == password) return false;
        if (gAuth.authorize(otpConfig.key, password)) {
            lastUsedPassword = password;
            return true;
        }
        return false;
    }

    public static class OtpConfig {

        private String key = null;
        private int validRange = 3;

        public int getValidRange() {
            return validRange;
        }

        public void setValidRange(int validRange) {
            this.validRange = validRange;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

    }

}
