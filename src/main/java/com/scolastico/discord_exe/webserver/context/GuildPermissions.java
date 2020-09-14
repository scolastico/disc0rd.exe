package com.scolastico.discord_exe.webserver.context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.etc.permissions.PermissionsData;
import com.scolastico.discord_exe.etc.permissions.PermissionsManager;
import com.scolastico.discord_exe.mysql.ServerSettings;
import com.scolastico.discord_exe.webserver.WebHandler;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.UUID;

@WebHandler.WebHandlerRegistration(context = {"/api/v1/guild/permissions/info", "/api/v1/guild/permissions/get", "/api/v1/guild/permissions/set/*", "/api/v1/guild/permissions/delete/*"})
public class GuildPermissions implements WebHandler {

    @Override
    public String onWebServer(HttpExchange httpExchange) {
        if (httpExchange.getRequestURI().getPath().equals("/api/v1/guild/permissions/info")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return info(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/guild/permissions/get")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return get(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().startsWith("/api/v1/guild/permissions/delete/")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return delete(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().startsWith("/api/v1/guild/permissions/set/")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return set(httpExchange);
        }
        return null;
    }

    private String info(HttpExchange httpExchange) {
        try {
            HashMap<String, String> permissions = PermissionsManager.getInstance().getPermissions();
            HashMap<String, Boolean> defaultValues = PermissionsManager.getInstance().getDefaultValues();
            Gson gson = new Gson();
            return "{\"status\":\"ok\",\"availablePermissions\":" + gson.toJson(permissions) + ",\"defaultValues\":" + gson.toJson(defaultValues) + "}";
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return "{\"status\":\"error\",\"error\":\"internal error\"}";
    }

    private String get(HttpExchange httpExchange) {
        try {
            Long guildId = GuildPanel.getIdFromAuthorization(httpExchange);
            if (guildId != null) {
                HashMap<UUID, PermissionsData> permissions = Disc0rd.getMysql().getServerSettings(guildId).getPermissionsData();
                Gson gson = new Gson();
                return "\"status\":\"ok\",\"get\":" + gson.toJson(permissions) + "}";
            }
            return "{\"status\":\"error\", \"error\":\"no auth\"}";
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return "{\"status\":\"error\",\"error\":\"internal error\"}";
    }

    private String delete(HttpExchange httpExchange) {
        try {
            String key = httpExchange.getRequestURI().getPath().replaceFirst("/api/v1/guild/permissions/set/", "");
            if (key.equals("")) return "{\"status\":\"error\", \"error\":\"request invalid\"}";
            Long guildId = GuildPanel.getIdFromAuthorization(httpExchange);
            if (guildId != null) {
                ServerSettings settings = Disc0rd.getMysql().getServerSettings(guildId);
                UUID uuid = UUID.fromString(key);
                if (settings.getPermissionsData().containsKey(uuid)) {
                    settings.getPermissionsData().remove(uuid);
                    Disc0rd.getMysql().setServerSettings(guildId, settings);
                } else {
                    return "{\"status\":\"error\", \"error\":\"uuid not found\"}";
                }
                return "\"status\":\"ok\"}";
            }
            return "{\"status\":\"error\", \"error\":\"no auth\"}";
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return "{\"status\":\"error\",\"error\":\"internal error\"}";
    }

    private String set(HttpExchange httpExchange) {
        try {
            String key = httpExchange.getRequestURI().getPath().replaceFirst("/api/v1/guild/permissions/set/", "");
            if (key.equals("")) return "{\"status\":\"error\", \"error\":\"request invalid\"}";
            Long guildId = GuildPanel.getIdFromAuthorization(httpExchange);
            if (guildId != null) {
                ServerSettings settings = Disc0rd.getMysql().getServerSettings(guildId);
                String json = Tools.getInstance().getJsonStringFromHttpExchange(httpExchange);
                if (json != null) {
                    try {
                        Gson gson = new Gson();
                        PermissionsData data = gson.fromJson(json, PermissionsData.class);
                        if (data != null) {
                            HashMap<UUID, PermissionsData> serverPermissionsData = settings.getPermissionsData();
                            if (key.equalsIgnoreCase("create")) {
                                if (settings.getPermissionsData().size() < Tools.getInstance().getLimitFromGuild(guildId).getPermissions()) {
                                    UUID uuid;
                                    do {
                                        uuid = UUID.randomUUID();
                                    } while (serverPermissionsData.containsKey(uuid));
                                    serverPermissionsData.put(uuid, data);
                                } else {
                                    return "{\"status\":\"error\", \"error\":\"limit reached\"}";
                                }
                            } else {
                                UUID uuid = UUID.fromString(key);
                                if (serverPermissionsData.containsKey(uuid)) {
                                    serverPermissionsData.remove(uuid);
                                    serverPermissionsData.put(uuid, data);
                                } else {
                                    return "{\"status\":\"error\", \"error\":\"uuid not found\"}";
                                }
                            }
                            settings.setPermissionsData(serverPermissionsData);
                            Disc0rd.getMysql().setServerSettings(guildId, settings);
                            return "\"status\":\"ok\"}";
                        }
                    } catch (JsonSyntaxException ignored) {}
                }
                return "{\"status\":\"error\", \"error\":\"json parse error\"}";
            }
            return "{\"status\":\"error\", \"error\":\"no auth\"}";
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return "{\"status\":\"error\",\"error\":\"internal error\"}";
    }

}
