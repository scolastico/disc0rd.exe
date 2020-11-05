package com.scolastico.discord_exe.webserver.context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.config.ConfigDataStore;
import com.scolastico.discord_exe.config.ConfigHandler;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.OtpHelper;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.webserver.WebHandler;
import com.sun.net.httpserver.HttpExchange;
import com.wrapper.spotify.SpotifyApi;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

@WebHandler.WebHandlerRegistration(
    context = {"/api/v1/admin/saveConfig/*", "/api/v1/admin/getConfig/*",
               "/api/v1/admin/saveSettings", "/api/v1/admin/getSettings",
               "/api/v1/admin/sendMessage", "/api/v1/admin/getUsername",
               "/api/v1/admin/getStatus", "/api/v1/admin/logout",
               "/api/v1/admin/login", "/api/v1/admin/isLoggedIn"})
public class OwnerPanel implements WebHandler {

  private static HashMap<String, Long> authKeys = new HashMap<>();
  private static HashMap<String, Long> authCookies = new HashMap<>();
  private final OtpHelper otpHelper = OtpHelper.getInstance();

  public static String getAuthCode() {
    clearHashMaps();
    String random;
    do {
      random = Tools.getInstance().getAlphaNumericString(16);
    } while (authKeys.containsKey(random));
    authKeys.put(random,
                 (System.currentTimeMillis() / 1000L) +
                     Disc0rd.getConfig().getOwnerPanel().getKeyValidTimeOut());
    return random;
  }

  private static void clearHashMaps() {
    for (String key : authKeys.keySet()) {
      if (authKeys.get(key) <= (System.currentTimeMillis() / 1000L)) {
        authKeys.remove(key);
      }
    }
    for (String key : authCookies.keySet()) {
      if (authCookies.get(key) <= (System.currentTimeMillis() / 1000L)) {
        authCookies.remove(key);
      }
    }
  }

  @Override
  public String onWebServer(HttpExchange httpExchange) {
    if (httpExchange.getRequestURI().getPath().equals(
            "/api/v1/admin/isLoggedIn")) {
      httpExchange.getResponseHeaders().set("Content-Type",
                                            "application/json; charset=utf-8;");
      if (isLoggedIn(httpExchange)) {
        return "{\"status\":\"ok\"}";
      } else {
        return "{\"status\":\"error\",\"error\":\"no auth\"}";
      }
    } else if (httpExchange.getRequestURI().getPath().equals(
                   "/api/v1/admin/login")) {
      httpExchange.getResponseHeaders().set("Content-Type",
                                            "application/json; charset=utf-8;");
      return login(httpExchange);
    } else if (httpExchange.getRequestURI().getPath().equals(
                   "/api/v1/admin/logout")) {
      httpExchange.getResponseHeaders().set("Content-Type",
                                            "application/json; charset=utf-8;");
      return logout(httpExchange);
    } else if (httpExchange.getRequestURI().getPath().equals(
                   "/api/v1/admin/getStatus")) {
      httpExchange.getResponseHeaders().set("Content-Type",
                                            "application/json; charset=utf-8;");
      return getStatus(httpExchange);
    } else if (httpExchange.getRequestURI().getPath().equals(
                   "/api/v1/admin/getUsername")) {
      httpExchange.getResponseHeaders().set("Content-Type",
                                            "application/json; charset=utf-8;");
      return getUsername(httpExchange);
    } else if (httpExchange.getRequestURI().getPath().equals(
                   "/api/v1/admin/sendMessage")) {
      httpExchange.getResponseHeaders().set("Content-Type",
                                            "application/json; charset=utf-8;");
      return sendMessage(httpExchange);
    } else if (httpExchange.getRequestURI().getPath().equals(
                   "/api/v1/admin/getSettings")) {
      httpExchange.getResponseHeaders().set("Content-Type",
                                            "application/json; charset=utf-8;");
      return getSettings(httpExchange);
    } else if (httpExchange.getRequestURI().getPath().equals(
                   "/api/v1/admin/saveSettings")) {
      httpExchange.getResponseHeaders().set("Content-Type",
                                            "application/json; charset=utf-8;");
      return saveSettings(httpExchange);
    } else if (httpExchange.getRequestURI().getPath().startsWith(
                   "/api/v1/admin/getConfig/")) {
      httpExchange.getResponseHeaders().set("Content-Type",
                                            "application/json; charset=utf-8;");
      return getConfig(httpExchange);
    } else if (httpExchange.getRequestURI().getPath().startsWith(
                   "/api/v1/admin/saveConfig/")) {
      httpExchange.getResponseHeaders().set("Content-Type",
                                            "application/json; charset=utf-8;");
      return saveConfig(httpExchange);
    }
    return null;
  }

  private String saveConfig(HttpExchange httpExchange) {
    try {
      if (isLoggedIn(httpExchange)) {
        String key = httpExchange.getRequestURI().getPath().replaceFirst(
            "/api/v1/admin/saveConfig/", "");
        if (otpHelper.isValid(Tools.getInstance().tryToParseInt(key))) {
          String jsonString =
              Tools.getInstance().getJsonStringFromHttpExchange(httpExchange);
          if (jsonString != null) {
            Gson gson = new Gson();
            Disc0rd.setConfig(gson.fromJson(jsonString, ConfigDataStore.class));
            ConfigHandler configHandler = Disc0rd.getConfigHandler();
            configHandler.setConfigObject(Disc0rd.getConfig());
            configHandler.saveConfigObject();
            return "{\"status\":\"ok\"}";
          }
          return "{\"status\":\"error\",\"error\":\"not supported\"}";
        } else {
          return "{\"status\":\"error\",\"error\":\"no otp auth\"}";
        }
      } else {
        return "{\"status\":\"error\",\"error\":\"no auth\"}";
      }
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
      return "{\"status\":\"error\",\"error\":\"internal error\"}";
    }
  }

  private String getConfig(HttpExchange httpExchange) {
    try {
      if (isLoggedIn(httpExchange)) {
        String key = httpExchange.getRequestURI().getPath().replaceFirst(
            "/api/v1/admin/getConfig/", "");
        if (otpHelper.isValid(Tools.getInstance().tryToParseInt(key))) {
          Gson gson = new GsonBuilder()
                          .registerTypeAdapter(
                              Long.class,
                              (JsonSerializer<Long>)(var, type,
                                                     jsonSerializationContext)
                                  -> new JsonPrimitive(String.valueOf(var)))
                          .create();
          return "{\"status\":\"ok\",\"config\":" +
              gson.toJson(Disc0rd.getConfig()) + "}";
        } else {
          return "{\"status\":\"error\",\"error\":\"no otp auth\"}";
        }
      } else {
        return "{\"status\":\"error\",\"error\":\"no auth\"}";
      }
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
      return "{\"status\":\"error\",\"error\":\"internal error\"}";
    }
  }

  private String saveSettings(HttpExchange httpExchange) {
    if (isLoggedIn(httpExchange)) {
      if (httpExchange.getRequestMethod().equals("POST")) {
        HashMap<String, String> postValues =
            Tools.getInstance().getPostValuesFromHttpExchange(httpExchange);
        if (postValues.containsKey("w2gDefaultPlayback") && postValues.containsKey("motd")) {
          try {
            Disc0rd.getConfig().setW2gDefaultPlayback(
                URLDecoder.decode(postValues.get("w2gDefaultPlayback"),
                                  StandardCharsets.UTF_8.toString()));
            Disc0rd.getConfig().setMotd(
                URLDecoder.decode(postValues.get("motd"),
                    StandardCharsets.UTF_8.toString()));
            if (postValues.get("twitch") == null) {
              Disc0rd.getConfig().setTwitchUrl("");
            } else {
              Disc0rd.getConfig().setTwitchUrl(
                  URLDecoder.decode(postValues.get("twitch"),
                      StandardCharsets.UTF_8.toString()));
            }
          } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
            return "{\"status\":\"error\",\"error\":\"internal error\"}";
          }
          ConfigHandler configHandler = Disc0rd.getConfigHandler();
          configHandler.setConfigObject(Disc0rd.getConfig());
          try {
            configHandler.saveConfigObject();
          } catch (IOException e) {
            ErrorHandler.getInstance().handle(e);
            return "{\"status\":\"error\",\"error\":\"internal error\"}";
          }
          Activity activity;
          if (Disc0rd.getConfig().getTwitchUrl().equals("")) {
            activity = Activity.playing(Disc0rd.getConfig().getMotd());
          } else {
            activity = Activity.streaming(Disc0rd.getConfig().getMotd(), Disc0rd.getConfig().getTwitchUrl());
          }
          Disc0rd.getJda().getPresence().setActivity(activity);
          return "{\"status\":\"ok\"}";
        }
      }
      return "{\"status\":\"error\",\"error\":\"not supported\"}";
    } else {
      return "{\"status\":\"error\",\"error\":\"no auth\"}";
    }
  }

  private String getSettings(HttpExchange httpExchange) {
    if (isLoggedIn(httpExchange)) {
      HashMap<String, String> json = new HashMap<>();
      json.put("w2gDefaultPlayback", Disc0rd.getConfig().getW2gDefaultPlayback());
      json.put("motd", Disc0rd.getConfig().getMotd());
      json.put("twitch", Disc0rd.getConfig().getTwitchUrl());
      return "{\"status\":\"ok\",\"settings\":" + new Gson().toJson(json) + "}";
    } else {
      return "{\"status\":\"error\",\"error\":\"no auth\"}";
    }
  }

  private String sendMessage(HttpExchange httpExchange) {
    if (isLoggedIn(httpExchange)) {
      if (httpExchange.getRequestMethod().equals("POST")) {
        HashMap<String, String> postValues =
            Tools.getInstance().getPostValuesFromHttpExchange(httpExchange);
        if (postValues.containsKey("message") &&
            postValues.containsKey("title")) {
          try {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(URLDecoder.decode(
                postValues.get("title"), StandardCharsets.UTF_8.toString()));
            embedBuilder.setDescription(URLDecoder.decode(
                postValues.get("message"), StandardCharsets.UTF_8.toString()));
            embedBuilder.setColor(Color.YELLOW);
            for (Guild guild : Disc0rd.getJda().getGuilds()) {
              if (guild.getTextChannels().size() != 0) {
                try {
                  guild.getTextChannels()
                      .get(0)
                      .sendMessage(embedBuilder.build())
                      .queue();
                } catch (Exception ignore) {
                }
              }
            }
          } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
          }
          return "{\"status\":\"ok\"}";
        }
      }
      return "{\"status\":\"error\",\"error\":\"not supported\"}";
    } else {
      return "{\"status\":\"error\",\"error\":\"no auth\"}";
    }
  }

  private String getUsername(HttpExchange httpExchange) {
    if (isLoggedIn(httpExchange)) {
      User user = Disc0rd.getJda().getUserById(
          Disc0rd.getConfig().getOwnerPanel().getOwnerId());
      if (user == null) {
        return "{\"status\":\"ok\",\"username\":\"Disc0rd.exe\"}";
      } else {
        return "{\"status\":\"ok\",\"username\":\"" + user.getName() + "\"}";
      }
    } else {
      return "{\"status\":\"error\",\"error\":\"no auth\"}";
    }
  }

  private String getStatus(HttpExchange httpExchange) {
    if (isLoggedIn(httpExchange)) {
      try {
        StringBuilder errors = new StringBuilder("[");
        for (Long key : ErrorHandler.getInstance().getErrorLog().keySet()) {
          errors.append("{\"time\":")
              .append(key)
              .append(",\"error\":\"")
              .append(URLEncoder.encode(
                  ErrorHandler.getInstance().getErrorLog().get(key),
                  StandardCharsets.UTF_8.toString()))
              .append("\"},");
        }
        if (errors.toString().endsWith(","))
          errors = new StringBuilder(errors.substring(0, errors.length() - 1));
        errors.append("]");
        return "{\"status\":\"ok\",\"statusInfo\":{\"serverCount\":" +
            Disc0rd.getJda().getGuilds().size() + ",\"executedCommands\":" +
            Disc0rd.getExecutedCommands().toString() +
            ",\"errorLog\":" + errors + "}}";
      } catch (Exception e) {
        ErrorHandler.getInstance().handle(e);
        return "{\"status\":\"error\",\"error\":\"internal error\"}";
      }
    } else {
      return "{\"status\":\"error\",\"error\":\"no auth\"}";
    }
  }

  private String logout(HttpExchange httpExchange) {
    if (isLoggedIn(httpExchange)) {
      authCookies = new HashMap<>();
      authKeys = new HashMap<>();
      return "{\"status\":\"ok\"}";
    } else {
      return "{\"status\":\"error\",\"error\":\"no auth\"}";
    }
  }

  private String login(HttpExchange httpExchange) {
    clearHashMaps();
    if (httpExchange.getRequestMethod().equals("POST")) {
      HashMap<String, String> postValues =
          Tools.getInstance().getPostValuesFromHttpExchange(httpExchange);
      if (postValues.containsKey("key")) {
        String key = postValues.get("key");
        if (authKeys.containsKey(key)) {
          authKeys.remove(key);
          String random;
          do {
            random = Tools.getInstance().getAlphaNumericString(32);
          } while (authCookies.containsKey(random));
          authCookies.put(random,
                          (System.currentTimeMillis() / 1000L) +
                              Disc0rd.getConfig().getOwnerPanel().getTimeOut());
          String secureCookie = "";
          if (Disc0rd.getConfig().getOwnerPanel().isSecureCookie()) {
            secureCookie = "; Secure";
          }
          httpExchange.getResponseHeaders().set(
              "Set-Cookie",
              "j_session_auth=" + random + "; path=/; Max-Age=" +
                  Disc0rd.getConfig().getOwnerPanel().getTimeOut() +
                  secureCookie);
          return "{\"status\":\"ok\",\"cookie\":\"" + random + "\"}";
        } else {
          return "{\"status\":\"error\",\"error\":\"no auth\"}";
        }
      }
    }
    return "{\"status\":\"error\",\"error\":\"not supported\"}";
  }

  private boolean isLoggedIn(HttpExchange httpExchange) {
    clearHashMaps();
    try {
      for (String cookie : httpExchange.getRequestHeaders().get("Cookie")) {
        if (cookie.contains("j_session_auth=")) {
          String key = cookie.split("j_session_auth=")[1].substring(0, 32);
          return authCookies.containsKey(key);
        }
      }
    } catch (Exception ignore) {
    }
    return false;
  }
}
