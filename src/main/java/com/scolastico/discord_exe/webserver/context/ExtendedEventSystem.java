package com.scolastico.discord_exe.webserver.context;

import com.google.gson.Gson;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEvent;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventManager;
import com.scolastico.discord_exe.event.extendedEventSystem.interfaces.Disc0rdAction;
import com.scolastico.discord_exe.event.extendedEventSystem.interfaces.Disc0rdEvent;
import com.scolastico.discord_exe.webserver.WebHandler;
import com.sun.net.httpserver.HttpExchange;

import java.util.ArrayList;
import java.util.HashMap;

@WebHandler.WebHandlerRegistration(context = {"/api/v1/guild/extendedEvent/info" , "/api/v1/guild/extendedEvent/get", "/api/v1/guild/extendedEvent/set", "/api/v1/guild/extendedEvent/create", "/api/v1/guild/extendedEvent/delete"})
public class ExtendedEventSystem implements WebHandler {

    private String infoResponseJSON = null;

    @Override
    public String onWebServer(HttpExchange httpExchange) {
        if (httpExchange.getRequestURI().getPath().equals("/api/v1/guild/extendedEvent/info")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return info(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/guild/extendedEvent/create")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return create(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/guild/extendedEvent/delete")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return delete(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/guild/extendedEvent/set")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return set(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/guild/extendedEvent/get")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return get(httpExchange);
        }
        return null;
    }

    private String get(HttpExchange httpExchange) {
        Long guildId = GuildPanel.getIdFromAuthorization(httpExchange);
        if (guildId != null) {
            ArrayList<ExtendedEvent> events = ExtendedEventManager.getInstance().getExtendedEvents(guildId);
            Gson gson = new Gson();
            return "{\"status\":\"ok\", \"events\":" + gson.toJson(events) + "}";
        } else {
            return "{\"status\":\"error\", \"error\":\"no auth\"}";
        }
    }

    private String set(HttpExchange httpExchange) {
        Long guildId = GuildPanel.getIdFromAuthorization(httpExchange);
        if (guildId != null) {
            HashMap<String, String> postValues = Tools.getInstance().getPostValuesFromHttpExchange(httpExchange);
            if (postValues.containsKey("name") && postValues.containsKey("actionsJson") && postValues.containsKey("eventConfigJson")) {
                ExtendedEvent extendedEvent = ExtendedEventManager.getInstance().getExtendedEvent(guildId, postValues.get("name"));
                if (extendedEvent != null) {
                    Gson gson = new Gson();
                    ActionJson actionJson = null;
                    EventJson eventJson = null;
                    try {
                        actionJson = gson.fromJson(postValues.get("actionsJson"), ActionJson.class);
                        eventJson = gson.fromJson(postValues.get("eventConfigJson"), EventJson.class);
                    } catch (Exception e) {
                        return "{\"status\":\"error\",\"error\":\"json error\", \"json_error\":\"" + e.getMessage().replaceAll("\"", "'") + "\"}";
                    }
                    if (actionJson == null || eventJson == null) return "{\"status\":\"error\",\"error\":\"json not valid\"}";
                    HashMap<Integer, ExtendedEvent.Action> actions = new HashMap<>();
                    for (Integer id:actionJson.getActions().keySet()) {
                        ActionConfig actionConfig = actionJson.getActions().get(id);
                        boolean exists = false;
                        for (Disc0rdAction action:ExtendedEventManager.getInstance().getActions().values()) {
                            if (action.getName().equals(actionConfig.getAction())) {
                                exists = true;
                                ExtendedEvent.Action newAction = new ExtendedEvent.Action();
                                newAction.setAction(action.getName());
                                HashMap<String, String> newActionConfig = new HashMap<>();
                                for (String configKey:action.getConfig().keySet()) {
                                    if (actionConfig.getConfig().containsKey(configKey)) {
                                        newActionConfig.put(configKey, actionConfig.getConfig().get(configKey));
                                    }
                                }
                                newAction.setConfig(newActionConfig);
                                actions.put(id, newAction);
                            }
                        }
                        if (!exists) {
                            return "{\"status\":\"error\",\"error\":\"action does not exists\"}";
                        }
                    }
                    extendedEvent.setActions(actions);
                    if (!ExtendedEventManager.getInstance().getEvents().containsKey(extendedEvent.getEvent())) {
                        return "{\"status\":\"error\",\"error\":\"internal error\"}";
                    }
                    HashMap<String, String> newEventConfig = new HashMap<>();
                    for (String configKey:ExtendedEventManager.getInstance().getEvents().get(extendedEvent.getEvent()).getConfig().keySet()) {
                        if (eventJson.getConfig().containsKey(configKey)) {
                            newEventConfig.put(configKey, eventJson.getConfig().get(configKey));
                        }
                    }
                    extendedEvent.setEventConfig(newEventConfig);
                    ExtendedEventManager.getInstance().setExtendedEvent(extendedEvent);
                    return "{\"status\":\"ok\"}";
                } else {
                    return "{\"status\":\"error\",\"error\":\"extended event not found\"}";
                }
            } else {
                return "{\"status\":\"error\",\"error\":\"not supported\"}";
            }
        } else {
            return "{\"status\":\"error\", \"error\":\"no auth\"}";
        }
    }

    private String delete(HttpExchange httpExchange) {
        Long guildId = GuildPanel.getIdFromAuthorization(httpExchange);
        if (guildId != null) {
            HashMap<String, String> postValues = Tools.getInstance().getPostValuesFromHttpExchange(httpExchange);
            if (postValues.containsKey("name")) {
                String name = postValues.get("name");
                ExtendedEvent extendedEvent = ExtendedEventManager.getInstance().getExtendedEvent(guildId, name);
                if (extendedEvent == null) return "{\"status\":\"error\",\"error\":\"not found\"}";
                ExtendedEventManager.getInstance().deleteExtendedEvent(extendedEvent);
                return "{\"status\":\"ok\"}";
            } else {
                return "{\"status\":\"error\",\"error\":\"not supported\"}";
            }
        } else {
            return "{\"status\":\"error\", \"error\":\"no auth\"}";
        }
    }

    private String create(HttpExchange httpExchange) {
        Long guildId = GuildPanel.getIdFromAuthorization(httpExchange);
        if (guildId != null) {
            HashMap<String, String> postValues = Tools.getInstance().getPostValuesFromHttpExchange(httpExchange);
            if (postValues.containsKey("name") && postValues.containsKey("event")) {
                String name = postValues.get("name");
                String event = postValues.get("event");
                for (ExtendedEvent extendedEvent:ExtendedEventManager.getInstance().getExtendedEvents(guildId)) {
                    if (extendedEvent.getName().equalsIgnoreCase(name)) {
                        return "{\"status\":\"error\", \"error\":\"name already exists\"}";
                    }
                }
                if (!ExtendedEventManager.getInstance().getEvents().containsKey(event)) {
                    return "{\"status\":\"error\", \"error\":\"event dont exist\"}";
                }
                ExtendedEvent extendedEvent = new ExtendedEvent();
                extendedEvent.setGuild(guildId);
                extendedEvent.setName(name);
                extendedEvent.setEvent(event);
                ExtendedEventManager.getInstance().setExtendedEvent(extendedEvent);
                return "{\"status\":\"ok\"}";
            } else {
                return "{\"status\":\"error\",\"error\":\"not supported\"}";
            }
        } else {
            return "{\"status\":\"error\", \"error\":\"no auth\"}";
        }
    }

    private String info(HttpExchange httpExchange) {
        if (GuildPanel.getIdFromAuthorization(httpExchange) != null) {
            if (infoResponseJSON == null) {
                Gson gson = new Gson();
                InfoResponse infoResponse = new InfoResponse();
                ArrayList<InfoResponse.Info> actionInfos = new ArrayList<>();
                for (Disc0rdAction action:ExtendedEventManager.getInstance().getActions().values()) {
                    InfoResponse.Info info = generateInfo(action.getName(), action.getDescription(), action.getConfig());
                    actionInfos.add(info);
                }
                infoResponse.setActionInfos(actionInfos);
                ArrayList<InfoResponse.Info> eventInfos = new ArrayList<>();
                for (Disc0rdEvent event:ExtendedEventManager.getInstance().getEvents().values()) {
                    InfoResponse.Info info = generateInfo(event.getName(), event.getDescription(), event.getConfig());
                    eventInfos.add(info);
                }
                infoResponse.setEventInfos(eventInfos);
                infoResponseJSON = gson.toJson(infoResponse);
            }
            return "{\"status\":\"ok\", \"info\":" + infoResponseJSON + "}";
        } else {
            return "{\"status\":\"error\", \"error\":\"no auth\"}";
        }
    }

    private InfoResponse.Info generateInfo(String name, String description, HashMap<String, String> config) {
        InfoResponse.Info info = new InfoResponse.Info();
        info.setName(name);
        info.setDescription(description);
        ArrayList<InfoResponse.Info.Config> configs = info.getConfig();
        for (String key:config.keySet()) {
            InfoResponse.Info.Config conf = new InfoResponse.Info.Config();
            conf.setName(key);
            conf.setDescription(config.get(key));
            configs.add(conf);
        }
        info.setConfig(configs);
        return info;
    }

    public static class EventJson {

        public HashMap<String, String> config = new HashMap<>();

        public HashMap<String, String> getConfig() {
            return config;
        }

        public void setConfig(HashMap<String, String> config) {
            this.config = config;
        }

    }

    public static class ActionJson {

        private HashMap<Integer, ActionConfig> actions = new HashMap<>();

        public HashMap<Integer, ActionConfig> getActions() {
            return actions;
        }

        public void setActions(HashMap<Integer, ActionConfig> actions) {
            this.actions = actions;
        }

    }

    public static class ActionConfig {

        private String action = "";
        private HashMap<String, String> config = new HashMap<>();

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

    public static class InfoResponse {

        private ArrayList<Info> actionInfos = new ArrayList<>();
        private ArrayList<Info> eventInfos = new ArrayList<>();

        public ArrayList<Info> getActionInfos() {
            return actionInfos;
        }

        public void setActionInfos(ArrayList<Info> actionInfos) {
            this.actionInfos = actionInfos;
        }

        public ArrayList<Info> getEventInfos() {
            return eventInfos;
        }

        public void setEventInfos(ArrayList<Info> eventInfos) {
            this.eventInfos = eventInfos;
        }

        public static class Info {

            private String name = "";
            private String description = "";
            private ArrayList<Config> config = new ArrayList<>();

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public ArrayList<Config> getConfig() {
                return config;
            }

            public void setConfig(ArrayList<Config> config) {
                this.config = config;
            }

            public static class Config {

                private String name = "";
                private String description = "";

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getDescription() {
                    return description;
                }

                public void setDescription(String description) {
                    this.description = description;
                }

            }

        }

    }

}
