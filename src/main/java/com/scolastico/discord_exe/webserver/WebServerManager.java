package com.scolastico.discord_exe.webserver;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.reflections.Reflections;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class WebServerManager implements HttpHandler {

    private static WebServerManager instance = null;
    private HttpServer httpServer;
    private HashMap<WebHandler, ArrayList<String>> webHandlers = new HashMap<WebHandler, ArrayList<String>>();
    private static final String error404 = "<div id=\"main\"><div class=\"fof\"><h1>Error 404</h1></div><style>*{transition:all .6s}html{height:100%}body{font-family:Lato,sans-serif;color:#888;margin:0}#main{display:table;width:100%;height:100vh;text-align:center}.fof{display:table-cell;vertical-align:middle}.fof h1{font-size:50px;display:inline-block;padding-right:12px;animation:type .5s alternate infinite}@keyframes type{from{box-shadow:inset -3px 0 0 #888}to{box-shadow:inset -3px 0 0 transparent}}</style></div>";

    public static WebServerManager getInstance() {
        if (instance == null) {
            instance = new WebServerManager();
        }
        return instance;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    private WebServerManager() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(Disc0rd.getConfig().getWebServer().getPort()), 0);

            DefaultWebHandler.getInstance().getAllPaths();

            Reflections reflections = new Reflections("com.scolastico.discord_exe");
            Set<Class<? extends WebHandler>> eventHandlers = reflections.getSubTypesOf(WebHandler.class);
            for (Class<?> eventHandler:eventHandlers) {
                Object obj = eventHandler.newInstance();
                if (obj instanceof WebHandler) {
                    WebHandler.WebHandlerRegistration annotation = obj.getClass().getDeclaredAnnotation(WebHandler.WebHandlerRegistration.class);
                    if (annotation == null) {
                        ErrorHandler.getInstance().handleFatal(new Exception("Missing WebHandlerRegistration annotation at '" + obj.getClass().getName() + "'!"));
                    } else {
                        ArrayList<String> context = new ArrayList<>(annotation.context().length);
                        context.addAll(Arrays.asList(annotation.context()));
                        webHandlers.put((WebHandler) obj, context);
                    }

                }
            }

            httpServer.createContext("/", this);
            httpServer.start();
        } catch (Exception e) {
            ErrorHandler.getInstance().handleFatal(e);
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            if (DefaultWebHandler.getInstance().getAllPaths().contains(httpExchange.getRequestURI().getPath())) {
                DefaultWebHandler.getInstance().onWebServer(httpExchange);
                httpExchange.close();
                return;
            }
            for (WebHandler webHandler:webHandlers.keySet()) for (String key:webHandlers.get(webHandler)) {
                if (key.endsWith("*")) {
                    if (httpExchange.getRequestURI().getPath().startsWith(key.substring(0, key.length()-1))) {
                        sendToHandler(httpExchange, webHandler);
                        return;
                    }
                } else {
                    if (key.equals(httpExchange.getRequestURI().getPath())) {
                        sendToHandler(httpExchange, webHandler);
                        return;
                    }
                }
            }
            httpExchange.sendResponseHeaders(404, error404.length());
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(error404.getBytes());
            outputStream.close();
            httpExchange.close();
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
    }

    private void sendToHandler(HttpExchange httpExchange, WebHandler webHandler) {
        try {
            String response = webHandler.onWebServer(httpExchange);
            if (response != null) {
                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream outputStream = httpExchange.getResponseBody();
                outputStream.write(response.getBytes());
                outputStream.close();
                httpExchange.close();
            }
            httpExchange.close();
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
    }

    public static String getError404() {
        return error404;
    }
}
