package com.scolastico.discord_exe.mysql;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ServerSettings {

    private boolean isSpecialServer = false;
    private HashMap<String, String> shortCuts = new HashMap<String, String>();
    private ColorNameConfig colorNameConfig = new ColorNameConfig();

    public ColorNameConfig getColorNameConfig() {
        return colorNameConfig;
    }

    public void setColorNameConfig(ColorNameConfig colorNameConfig) {
        this.colorNameConfig = colorNameConfig;
    }

    public boolean isSpecialServer() {
        return isSpecialServer;
    }

    public void setSpecialServer(boolean specialServer) {
        isSpecialServer = specialServer;
    }

    public HashMap<String, String> getShortCuts() {
        return shortCuts;
    }

    public void setShortCuts(HashMap<String, String> shortCuts) {
        this.shortCuts = shortCuts;
    }

    public static class ColorNameConfig {
        private boolean isEnabled = false;
        private int sensitivity = 30;
        private List<String> disabledColors = Arrays.asList("#ffffff", "#2C2F33", "#23272A");

        public List<String> getDisabledColors() {
            return disabledColors;
        }

        public void setDisabledColors(List<String> disabledColors) {
            this.disabledColors = disabledColors;
        }

        public int getSensitivity() {
            return sensitivity;
        }

        public void setSensitivity(int sensitivity) {
            this.sensitivity = sensitivity;
        }

        private HashMap<Long, Long> roles = new HashMap<>();

        public boolean isEnabled() {
            return isEnabled;
        }

        public void setEnabled(boolean enabled) {
            isEnabled = enabled;
        }

        public HashMap<Long, Long> getRoles() {
            return roles;
        }

        public void setRoles(HashMap<Long, Long> roles) {
            this.roles = roles;
        }
    }

}
