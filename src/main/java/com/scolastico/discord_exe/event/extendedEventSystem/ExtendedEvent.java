package com.scolastico.discord_exe.event.extendedEventSystem;

import java.util.HashMap;

public class ExtendedEvent {

    private Long guild;
    private String name;
    private String event;
    private HashMap<String, String> eventConfig = new HashMap<>();
    private HashMap<Integer, Action> actions = new HashMap<>();

    public Long getGuild() {
        return guild;
    }

    public void setGuild(Long guild) {
        this.guild = guild;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public HashMap<String, String> getEventConfig() {
        return eventConfig;
    }

    public void setEventConfig(HashMap<String, String> eventConfig) {
        this.eventConfig = eventConfig;
    }

    public HashMap<Integer, Action> getActions() {
        return actions;
    }

    public void setActions(HashMap<Integer, Action> actions) {
        this.actions = actions;
    }

    public static class Action {

        private String action;
        private HashMap<String, String> config;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public HashMap<String, String> getConfig() {
            return config;
        }

        public void setConfig(HashMap<String, String> config) {
            this.config = config;
        }

    }

}
