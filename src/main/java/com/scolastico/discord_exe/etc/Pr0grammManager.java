package com.scolastico.discord_exe.etc;

import com.scolastico.discord_exe.config.ConfigDataStore;
import com.scolastico.discord_exe.config.ConfigHandler;
import com.scolastico.discord_exe.config.Pr0grammConfigDataStore;

public class Pr0grammManager {

    private static Pr0grammManager instance = null;
    private Pr0grammAPI pr0grammAPI;
    private ConfigHandler configHandler;
    private Pr0grammConfigDataStore config;

    private Pr0grammManager() {
        try {
            configHandler = new ConfigHandler(new Pr0grammConfigDataStore(), "pr0gramm.json", true);
            Object obj = configHandler.getConfigObject();
            if (obj instanceof Pr0grammConfigDataStore) {
                config = (Pr0grammConfigDataStore) obj;
            } else {
                throw new Exception("Config not valid! Please delete your config and try again!");
            }
            if (config.getToken() == null || config.getUsername() == null || config.getPassword() == null) throw new Exception("Config not valid! Please delete your config and try again!");
            try {
                pr0grammAPI = new Pr0grammAPI(config.getToken());
            } catch (Pr0grammAPI.Pr0grammLoginError error) {
                pr0grammAPI = new Pr0grammAPI(config.getUsername(), config.getPassword());
                config.setToken(pr0grammAPI.getToken());
                configHandler.setConfigObject(config);
                configHandler.saveConfigObject();
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handleFatal(e);
        }
    }

    public static Pr0grammManager getInstance() {
        if (instance == null) {
            instance = new Pr0grammManager();
        }
        return instance;
    }

    public Pr0grammAPI getPr0grammAPI() {
        return pr0grammAPI;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public Pr0grammConfigDataStore getConfig() {
        return config;
    }
}
