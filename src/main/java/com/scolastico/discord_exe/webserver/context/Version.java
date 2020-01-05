package com.scolastico.discord_exe.webserver.context;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.webserver.WebHandler;
import com.sun.net.httpserver.HttpExchange;

@WebHandler.WebHandlerRegistration(context = {"/version"})
public class Version implements WebHandler {
    @Override
    public String onWebServer(HttpExchange httpExchange) {
        httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
        return "{\"version\":\"" + Disc0rd.getVersion() + "\"}";
    }
}
