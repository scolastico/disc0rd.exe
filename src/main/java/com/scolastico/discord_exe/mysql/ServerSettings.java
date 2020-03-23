package com.scolastico.discord_exe.mysql;

import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ServerSettings {

    private boolean isSpecialServer = false;
    private HashMap<String, String> shortCuts = new HashMap<String, String>();
    private ColorNameConfig colorNameConfig = new ColorNameConfig();
    private ServerLimits serverLimits = new ServerLimits();
    private String log = "[LOG BEGINNING]";
    private ArrayList<ExtendedEvent> extendedEvents = new ArrayList<>();
    private String cmdPrefix = "$";

    public String getCmdPrefix() {
        return cmdPrefix;
    }

    public void setCmdPrefix(String cmdPrefix) {
        this.cmdPrefix = cmdPrefix;
    }

    public ArrayList<ExtendedEvent> getExtendedEvents() {
        return extendedEvents;
    }

    public void setExtendedEvents(ArrayList<ExtendedEvent> extendedEvents) {
        this.extendedEvents = extendedEvents;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public ServerLimits getServerLimits() {
        return serverLimits;
    }

    public void setServerLimits(ServerLimits serverLimits) {
        this.serverLimits = serverLimits;
    }

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
        private HashMap<Long, Long> roles = new HashMap<>();

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

    public static class ServerLimits {
        private int logLines = 0;
        private int events = 0;
        private int actionsPerEvent = 0;

        public int getLogLines() {
            return logLines;
        }

        public void setLogLines(int logLines) {
            this.logLines = logLines;
        }

        public int getEvents() {
            return events;
        }

        public void setEvents(int events) {
            this.events = events;
        }

        public int getActionsPerEvent() {
            return actionsPerEvent;
        }

        public void setActionsPerEvent(int actionsPerEvent) {
            this.actionsPerEvent = actionsPerEvent;
        }
    }

}
