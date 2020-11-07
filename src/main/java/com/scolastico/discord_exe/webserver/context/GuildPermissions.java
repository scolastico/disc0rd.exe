package com.scolastico.discord_exe.webserver.context;

import com.google.gson.*;
import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.etc.permissions.PermissionsData;
import com.scolastico.discord_exe.etc.permissions.PermissionsManager;
import com.scolastico.discord_exe.mysql.ServerSettings;
import com.scolastico.discord_exe.webserver.WebHandler;
import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@WebHandler.
WebHandlerRegistration(context = {"/api/v1/guild/permissions/info",
                                  "/api/v1/guild/permissions/get",
                                  "/api/v1/guild/permissions/set/*",
                                  "/api/v1/guild/permissions/delete/*"})
public class GuildPermissions implements WebHandler {

  @Override
  public String onWebServer(HttpExchange httpExchange) {
    if (httpExchange.getRequestURI().getPath().equals(
            "/api/v1/guild/permissions/info")) {
      httpExchange.getResponseHeaders().set("Content-Type",
                                            "application/json; charset=utf-8;");
      return info(httpExchange);
    } else if (httpExchange.getRequestURI().getPath().equals(
                   "/api/v1/guild/permissions/get")) {
      httpExchange.getResponseHeaders().set("Content-Type",
                                            "application/json; charset=utf-8;");
      return get(httpExchange);
    } else if (httpExchange.getRequestURI().getPath().startsWith(
                   "/api/v1/guild/permissions/delete/")) {
      httpExchange.getResponseHeaders().set("Content-Type",
                                            "application/json; charset=utf-8;");
      return delete(httpExchange);
    } else if (httpExchange.getRequestURI().getPath().startsWith(
                   "/api/v1/guild/permissions/set/")) {
      httpExchange.getResponseHeaders().set("Content-Type",
                                            "application/json; charset=utf-8;");
      return set(httpExchange);
    }
    return null;
  }

  private String info(HttpExchange httpExchange) {
    try {
      HashMap<String, String> permissions =
          PermissionsManager.getInstance().getPermissions();
      HashMap<String, Boolean> defaultValues =
          PermissionsManager.getInstance().getDefaultValues();
      Gson gson = new Gson();
      return "{\"status\":\"ok\",\"availablePermissions\":" +
          gson.toJson(permissions) +
          ",\"defaultValues\":" + gson.toJson(defaultValues) + "}";
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
    return "{\"status\":\"error\",\"error\":\"internal error\"}";
  }

  private String get(HttpExchange httpExchange) {
    try {
      Long guildId = GuildPanel.getIdFromAuthorization(httpExchange);
      if (guildId != null) {
        HashMap<UUID, PermissionsData> permissions =
            Disc0rd.getMysql().getServerSettings(guildId).getPermissionsData();
        Gson gson =
            new GsonBuilder()
                .registerTypeAdapter(
                    Long.class,
                    (JsonSerializer<Long>)(var, type, jsonSerializationContext)
                        -> new JsonPrimitive(String.valueOf(var)))
                .create();
        if (permissions.size() != 0) {
          return "{\"status\":\"ok\",\"get\":" + gson.toJson(permissions) + "}";
        } else {
          HashMap<String, PermissionsData> tmp = new HashMap<>();
          PermissionsData defaultData = new PermissionsData(0);
          defaultData.setPermissions(
              PermissionsManager.getInstance().getDefaultValues());
          tmp.put("create", defaultData);
          return "{\"status\":\"ok\",\"get\":" + gson.toJson(tmp) + "}";
        }
      }
      return "{\"status\":\"error\", \"error\":\"no auth\"}";
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
    return "{\"status\":\"error\",\"error\":\"internal error\"}";
  }

  private String delete(HttpExchange httpExchange) {
    try {
      String key = httpExchange.getRequestURI().getPath().replaceFirst(
          "/api/v1/guild/permissions/delete/", "");
      if (key.equals(""))
        return "{\"status\":\"error\", \"error\":\"request invalid\"}";
      Long guildId = GuildPanel.getIdFromAuthorization(httpExchange);
      if (guildId != null) {
        ServerSettings settings = Disc0rd.getMysql().getServerSettings(guildId);
        UUID uuid = UUID.fromString(key);
        if (settings.getPermissionsData().containsKey(uuid)) {
          settings.getPermissionsData().remove(uuid);
        } else {
          return "{\"status\":\"error\", \"error\":\"uuid not found\"}";
        }
        return "{\"status\":\"ok\"}";
      }
      return "{\"status\":\"error\", \"error\":\"no auth\"}";
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
    return "{\"status\":\"error\",\"error\":\"internal error\"}";
  }

  private String set(HttpExchange httpExchange) {
    try {
      String key = httpExchange.getRequestURI().getPath().replaceFirst(
          "/api/v1/guild/permissions/set/", "");
      if (key.equals(""))
        return "{\"status\":\"error\", \"error\":\"request invalid\"}";
      Long guildId = GuildPanel.getIdFromAuthorization(httpExchange);
      if (guildId != null) {
        ServerSettings settings = Disc0rd.getMysql().getServerSettings(guildId);
        String json =
            Tools.getInstance().getJsonStringFromHttpExchange(httpExchange);
        if (json != null) {
          try {
            Gson gson = new Gson();
            PermissionsData data = gson.fromJson(json, PermissionsData.class);
            if (data != null) {
              HashMap<String, Boolean> permissions = data.getPermissions();
              ArrayList<String> toDelete = new ArrayList<>();
              for (String permission : permissions.keySet()) {
                if (!PermissionsManager.getInstance()
                         .getPermissions()
                         .containsKey(permission)) {
                  toDelete.add(permission);
                }
              }
              for (String permission : toDelete) {
                permissions.remove(permission);
              }
              data.setPermissions(permissions);
              HashMap<UUID, PermissionsData> serverPermissionsData =
                  settings.getPermissionsData();
              UUID tmpUUID = null;
              if (key.equalsIgnoreCase("create")) {
                if (settings.getPermissionsData().size() <
                    Tools.getInstance()
                        .getLimitFromGuild(guildId)
                        .getPermissions()) {
                  UUID uuid;
                  do {
                    uuid = UUID.randomUUID();
                  } while (serverPermissionsData.containsKey(uuid));
                  serverPermissionsData.put(uuid, data);
                  tmpUUID = uuid;
                } else {
                  return "{\"status\":\"error\", \"error\":\"limit reached\"}";
                }
              } else {
                UUID uuid = UUID.fromString(key);
                if (serverPermissionsData.containsKey(uuid)) {
                  serverPermissionsData.remove(uuid);
                  serverPermissionsData.put(uuid, data);
                  tmpUUID = uuid;
                } else {
                  return "{\"status\":\"error\", \"error\":\"uuid not found\"}";
                }
              }
              settings.setPermissionsData(serverPermissionsData);
              return "{\"status\":\"ok\",\"uuid\":\"" + tmpUUID.toString() +
                  "\"}";
            }
          } catch (JsonSyntaxException ignored) {
          }
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
