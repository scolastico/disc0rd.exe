package com.scolastico.discord_exe.webserver.context;

import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.event.extendedEventSystem.events.OnWebhook;
import com.scolastico.discord_exe.webserver.WebHandler;
import com.sun.net.httpserver.HttpExchange;

@WebHandler.WebHandlerRegistration(context = {"/api/v1/webhook/*"})
public class ExtendedEventWebHook implements WebHandler {
    @Override
    public String onWebServer(HttpExchange httpExchange) {
        try {
            if (httpExchange.getRequestURI().getPath().startsWith("/api/v1/webhook/")) {
                httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
                try {
                    String path = httpExchange.getRequestURI().getPath().replaceFirst("/api/v1/webhook/", "");
                    if (path.equals("") || path.split("/").length == 1) {
                        return "{\"status\":\"error\", \"error\":\"request invalid\"}";
                    }
                    String guildId = path.split("/")[0];
                    path = path.replaceFirst(guildId + "/", "");
                    OnWebhook.writeRequest(httpExchange, guildId, path);
                    return "{\"status\":\"ok\"}";
                } catch (Exception ignored) {
                    return "{\"status\":\"error\",\"error\":\"internal error\"}";
                }
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return null;
    }
}
