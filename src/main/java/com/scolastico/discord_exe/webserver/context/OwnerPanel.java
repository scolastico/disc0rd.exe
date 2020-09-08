package com.scolastico.discord_exe.webserver.context;

import com.google.gson.Gson;
import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.config.ConfigDataStore;
import com.scolastico.discord_exe.config.ConfigHandler;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.OtpHelper;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.webserver.WebHandler;
import com.sun.net.httpserver.HttpExchange;
import com.wrapper.spotify.SpotifyApi;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@WebHandler.WebHandlerRegistration(context = {
        "/api/v1/admin/saveConfig/*",
        "/api/v1/admin/getConfig/*",
        "/api/v1/admin/saveSettings",
        "/api/v1/admin/getSettings",
        "/api/v1/admin/sendMessage",
        "/api/v1/admin/getUsername",
        "/api/v1/admin/getStatus",
        "/api/v1/admin/logout",
        "/api/v1/admin/login",
        "/api/v1/admin/isLoggedIn",
        "/api/v1/admin/spotify",
        "api/v1/admin/spotifyLogin"
})
public class OwnerPanel implements WebHandler {

    private static HashMap<String, Long> authKeys = new HashMap<>();
    private static HashMap<String, Long> authCookies = new HashMap<>();
    private OtpHelper otpHelper = OtpHelper.getInstance();
    private static final String success_page = "<!doctype html><title>Success!</title><style>body{text-align: center; padding: 150px;}h1{font-size: 50px;}body{font: 20px Helvetica, sans-serif; color: #333;}article{display: block; text-align: left; width: 650px; margin: 0 auto;}a{color: #dc8100; text-decoration: none;}a:hover{color: #333; text-decoration: none;}</style><article> <h1>Success!</h1> <div> <p>You have been successfully logged in via Spotify! You can now safely leave this page and concentrate on Discord again!</p></div></article>";

    public static String getAuthCode() {
        clearHashMaps();
        String random;
        do {
            random = Tools.getInstance().getAlphaNumericString(16);
        } while (authKeys.containsKey(random));
        authKeys.put(random, (System.currentTimeMillis() / 1000L) + Disc0rd.getConfig().getOwnerPanel().getKeyValidTimeOut());
        return random;
    }

    private static void clearHashMaps() {
        for (String key:authKeys.keySet()) {
            if (authKeys.get(key) <= (System.currentTimeMillis() / 1000L)) {
                authKeys.remove(key);
            }
        }
        for (String key:authCookies.keySet()) {
            if (authCookies.get(key) <= (System.currentTimeMillis() / 1000L)) {
                authCookies.remove(key);
            }
        }
    }

    @Override
    public String onWebServer(HttpExchange httpExchange) {
        if (httpExchange.getRequestURI().getPath().equals("/api/v1/admin/isLoggedIn")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            if (isLoggedIn(httpExchange)) {
                return "{\"status\":\"ok\"}";
            } else {
                return "{\"status\":\"error\",\"error\":\"no auth\"}";
            }
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/admin/login")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return login(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/admin/logout")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return logout(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/admin/getStatus")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return getStatus(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/admin/getUsername")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return getUsername(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/admin/sendMessage")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return sendMessage(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/admin/getSettings")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return getSettings(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/admin/saveSettings")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return saveSettings(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().startsWith("/api/v1/admin/getConfig/")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return getConfig(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().startsWith("/api/v1/admin/saveConfig/")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return saveConfig(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/admin/spotify")) {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return spotify(httpExchange);
        } else if (httpExchange.getRequestURI().getPath().equals("/api/v1/admin/spotifyLogin")) {
            return spotifyLogin(httpExchange);
        }
        return null;
    }

    private String spotify(HttpExchange httpExchange) {
        try {
            if (isLoggedIn(httpExchange)) {

                return "{\"status\":\"ok\",\"url\":\"\"}";
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
            return "{\"status\":\"error\",\"error\":\"internal error\"}";
        }
        return "{\"status\":\"error\",\"error\":\"no auth\"}";
    }

    private String spotifyLogin(HttpExchange httpExchange) {
        try {
            if (isLoggedIn(httpExchange)) {

            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
            return "{\"status\":\"error\",\"error\":\"internal error\"}";
        }
        httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
        return "{\"status\":\"error\",\"error\":\"no auth\"}";
    }

    private String saveConfig(HttpExchange httpExchange) {
        try {
            if (isLoggedIn(httpExchange)) {
                String key = httpExchange.getRequestURI().getPath().replaceFirst("/api/v1/admin/saveConfig/", "");
                if (otpHelper.isValid(Tools.getInstance().tryToParseInt(key))) {
                    if (httpExchange.getRequestMethod().equals("POST")) {
                        HashMap<String, String> postValues = Tools.getInstance().getPostValuesFromHttpExchange(httpExchange);
                        if (postValues.containsKey("config")) {
                            Gson gson = new Gson();
                            Disc0rd.setConfig(gson.fromJson(URLDecoder.decode(postValues.get("config"), StandardCharsets.UTF_8.toString()), ConfigDataStore.class));
                            ConfigHandler configHandler = Disc0rd.getConfigHandler();
                            configHandler.setConfigObject(Disc0rd.getConfig());
                            configHandler.saveConfigObject();
                            return "{\"status\":\"ok\"}";
                        }
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
                String key = httpExchange.getRequestURI().getPath().replaceFirst("/api/v1/admin/getConfig/", "");
                if (otpHelper.isValid(Tools.getInstance().tryToParseInt(key))) {
                    Gson gson = new Gson();
                    return "{\"status\":\"ok\",\"config\":\"" + URLEncoder.encode(gson.toJson(Disc0rd.getConfig()), StandardCharsets.UTF_8.toString()) + "\"}";
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
                HashMap<String, String> postValues = Tools.getInstance().getPostValuesFromHttpExchange(httpExchange);
                if (postValues.containsKey("w2gDefaultPlayback")) {
                    try {
                        Disc0rd.getConfig().setW2gDefaultPlayback(URLDecoder.decode(postValues.get("w2gDefaultPlayback"), StandardCharsets.UTF_8.toString()));
                    } catch (Exception e) {
                        ErrorHandler.getInstance().handle(e);
                    }
                    ConfigHandler configHandler = Disc0rd.getConfigHandler();
                    configHandler.setConfigObject(Disc0rd.getConfig());
                    try {
                        configHandler.saveConfigObject();
                    } catch (IOException e) {
                        ErrorHandler.getInstance().handle(e);
                        return "{\"status\":\"error\",\"error\":\"internal error\"}";
                    }
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
            return "{\"status\":\"ok\",\"settings\":{\"w2gDefaultPlayback\":\"" + Disc0rd.getConfig().getW2gDefaultPlayback() + "\"}}";
        } else {
            return "{\"status\":\"error\",\"error\":\"no auth\"}";
        }
    }

    private String sendMessage(HttpExchange httpExchange) {
        if (isLoggedIn(httpExchange)) {
            if (httpExchange.getRequestMethod().equals("POST")) {
                HashMap<String, String> postValues = Tools.getInstance().getPostValuesFromHttpExchange(httpExchange);
                if (postValues.containsKey("message") && postValues.containsKey("title")) {
                    try {
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle(URLDecoder.decode(postValues.get("title"), StandardCharsets.UTF_8.toString()));
                        embedBuilder.setDescription(URLDecoder.decode(postValues.get("message"), StandardCharsets.UTF_8.toString()));
                        embedBuilder.setFooter("This message deletes itself in 120 seconds!");
                        embedBuilder.setColor(Color.YELLOW);
                        for (Guild guild:Disc0rd.getJda().getGuilds()) {
                            if (guild.getTextChannels().size() != 0) {
                                try {
                                    guild.getTextChannels().get(0).sendMessage(embedBuilder.build()).complete().delete().queueAfter(120, TimeUnit.SECONDS);
                                } catch (Exception ignore) {}
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
            User user = Disc0rd.getJda().getUserById(Disc0rd.getConfig().getOwnerPanel().getOwnerId());
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
                for (Long key:ErrorHandler.getInstance().getErrorLog().keySet()) {
                    errors.append("{\"time\":").append(key).append(",\"error\":\"").append(URLEncoder.encode(ErrorHandler.getInstance().getErrorLog().get(key), StandardCharsets.UTF_8.toString())).append("\"},");
                }
                if (errors.toString().endsWith(",")) errors = new StringBuilder(errors.substring(0, errors.length() - 1));
                errors.append("]");
                return "{\"status\":\"ok\",\"statusInfo\":{\"serverCount\":" + Disc0rd.getJda().getGuilds().size() + ",\"executedCommands\":" + Disc0rd.getExecutedCommands().toString() + ",\"errorLog\":" + errors + "}}";
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
            HashMap<String, String> postValues = Tools.getInstance().getPostValuesFromHttpExchange(httpExchange);
            if (postValues.containsKey("key")) {
                String key = postValues.get("key");
                if (authKeys.containsKey(key)) {
                    authKeys.remove(key);
                    String random;
                    do {
                        random = Tools.getInstance().getAlphaNumericString(32);
                    } while (authCookies.containsKey(random));
                    authCookies.put(random, (System.currentTimeMillis() / 1000L) + Disc0rd.getConfig().getOwnerPanel().getTimeOut());
                    String secureCookie = "";
                    if (Disc0rd.getConfig().getOwnerPanel().isSecureCookie()) {
                        secureCookie = "; Secure";
                    }
                    httpExchange.getResponseHeaders().set("Set-Cookie", "j_session_auth=" + random + "; path=/; Max-Age=" + Disc0rd.getConfig().getOwnerPanel().getTimeOut() + secureCookie);
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
            for (String cookie:httpExchange.getRequestHeaders().get("Cookie")) {
                if (cookie.contains("j_session_auth=")) {
                    String key = cookie.split("j_session_auth=")[1].substring(0, 32);
                    return authCookies.containsKey(key);
                }
            }
        } catch (Exception ignore) {}
        return false;
    }
}
