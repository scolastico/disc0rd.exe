package com.scolastico.discord_exe.webserver.context;

import com.google.gson.Gson;
import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.mysql.ServerSettings;
import com.scolastico.discord_exe.webserver.WebHandler;
import com.sun.net.httpserver.HttpExchange;
import net.dv8tion.jda.api.entities.Guild;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@WebHandler.WebHandlerRegistration(context = {"/api/v1/guild/login", "/api/v1/guild/logout", "/api/v1/guild/info"})
public class GuildPanel implements WebHandler {

    private static ArrayList<Authorization> authorizations = new ArrayList<Authorization>();
    private static ArrayList<Authorization> authTokens = new ArrayList<Authorization>();

    public static String getAuthToken(long guildId) {
        removeOldData();
        ArrayList<String> givenKeys = new ArrayList<>();
        for (Authorization auth:authTokens) {
            givenKeys.add(auth.getToken());
        }
        String random;
        do {
            random = Tools.getInstance().getAlphaNumericString(16);
        } while (givenKeys.contains(random));
        authTokens.add(new Authorization((System.currentTimeMillis() / 1000L) + Disc0rd.getConfig().getAdminPanel().getKeyValidTimeOut(), guildId, random));
        return random;
    }

    public static Long getIdFromAuthorization(HttpExchange httpExchange) {
        removeOldData();
        try {
            for (String cookie:httpExchange.getRequestHeaders().get("Cookie")) {
                if (cookie.contains("j_session_auth=")) {
                    String key = cookie.split("j_session_auth=")[1].substring(0, 32);
                    for (Authorization authorization:authorizations) {
                        if (authorization.getToken().equals(key)) {
                            return authorization.guildId;
                        }
                    }
                }
            }
        } catch (Exception ignore) {}
        return null;
    }

    private static void removeOldData() {
        authorizations.removeIf(authorization -> authorization.getTimeStamp() <= (System.currentTimeMillis() / 1000L));
        authorizations.removeIf(authorization -> Disc0rd.getJda().getGuildById(authorization.getGuildId()) == null);
        authTokens.removeIf(authorization -> authorization.getTimeStamp() <= (System.currentTimeMillis() / 1000L));
        authTokens.removeIf(authorization -> Disc0rd.getJda().getGuildById(authorization.getGuildId()) == null);
    }

    @Override
    public String onWebServer(HttpExchange httpExchange) {
        if (httpExchange.getRequestURI().getPath().equals("/api/v1/guild/login")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return login(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/guild/logout")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return logout(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/guild/info")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return info(httpExchange);
        }
        return null;
    }

    private String login(HttpExchange httpExchange) {
        removeOldData();
        if (httpExchange.getRequestMethod().equals("POST")) {
            HashMap<String, String> postValues = Tools.getInstance().getPostValuesFromHttpExchange(httpExchange);
            if (postValues.containsKey("key")) {
                String key = postValues.get("key");
                for (Authorization authorization:authTokens) {
                    if (authorization.getToken().equals(key)) {
                        authTokens.remove(authorization);
                        ArrayList<String> givenKeys = new ArrayList<>();
                        for (Authorization auth:authorizations) {
                            givenKeys.add(auth.getToken());
                        }
                        String random;
                        do {
                            random = Tools.getInstance().getAlphaNumericString(32);
                        } while (givenKeys.contains(random));
                        authorizations.add(new Authorization((System.currentTimeMillis() / 1000L) + Disc0rd.getConfig().getAdminPanel().getTimeOut(), authorization.guildId, random));
                        httpExchange.getResponseHeaders().set("Set-Cookie", "j_session_auth=" + random + "; path=/; Max-Age=" + Disc0rd.getConfig().getAdminPanel().getTimeOut() + Disc0rd.getConfig().getAdminPanel().isSecureCookie());
                        return "{\"status\":\"ok\",\"cookie\":\"" + random + "\"}";
                    }
                }
            }
        }
        return "{\"status\":\"error\", \"error\":\"no auth\"}";
    }

    private String logout(HttpExchange httpExchange) {
        if (httpExchange.getRequestHeaders().get("Cookie") != null) {
            for (String cookie : httpExchange.getRequestHeaders().get("Cookie")) {
                if (cookie.contains("j_session_auth=")) {
                    String key = cookie.split("j_session_auth=")[1].substring(0, 32);
                    for (Authorization authorization:authorizations) {
                        if (authorization.getToken().equals(key)) {
                            authorizations.remove(authorization);
                            authorizations.removeIf(auth -> auth.getGuildId().equals(authorization.getGuildId()));
                            authTokens.removeIf(auth -> auth.getGuildId().equals(authorization.getGuildId()));
                            return "{\"status\":\"ok\"}";
                        }
                    }
                }
            }
        }
        return "{\"status\":\"error\", \"error\":\"no auth\"}";
    }

    private String info(HttpExchange httpExchange) {
        try {
            Long id = getIdFromAuthorization(httpExchange);
            if (id != null) {
                Guild guild = Disc0rd.getJda().getGuildById(id);
                if (guild == null) return "{\"status\":\"error\", \"error\":\"no auth\"}";
                Gson gson = new Gson();
                ServerSettings.ServerLimits limits = Tools.getInstance().getLimitFromGuild(id);
                return "{\"status\":\"ok\",\"image\":\"" + URLEncoder.encode(Objects.toString(guild.getIconUrl()), StandardCharsets.UTF_8.toString()) + "\",\"name\":\"" + URLEncoder.encode(guild.getName(), StandardCharsets.UTF_8.toString()) + "\", \"limits\": " + gson.toJson(limits) + "}";
            }
            return "{\"status\":\"error\", \"error\":\"no auth\"}";
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return "{\"status\":\"error\", \"error\":\"internal error\"}";
    }

    private static class Authorization {
        public Long timeStamp;
        public Long guildId;
        public String token;

        public Authorization(Long timeStamp, Long guildId, String token) {
            this.timeStamp = timeStamp;
            this.guildId = guildId;
            this.token = token;
        }

        public Long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(Long timeStamp) {
            this.timeStamp = timeStamp;
        }

        public Long getGuildId() {
            return guildId;
        }

        public void setGuildId(Long guildId) {
            this.guildId = guildId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

}
