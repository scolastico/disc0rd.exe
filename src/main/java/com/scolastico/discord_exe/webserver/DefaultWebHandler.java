package com.scolastico.discord_exe.webserver;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.sun.net.httpserver.HttpExchange;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.*;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class DefaultWebHandler {

    private static DefaultWebHandler instance = null;
    private HashMap<String, String> paths = new HashMap<>();

    public static DefaultWebHandler getInstance() {
        if (instance == null) {
            instance = new DefaultWebHandler();
        }
        return instance;
    }

    private DefaultWebHandler() {
        Reflections reflections = new Reflections("webServer", new ResourcesScanner());
        Set<String> resourceList = reflections.getResources(x -> true);
        ArrayList<String> filesRaw = new ArrayList<>();
        for (String path:resourceList) filesRaw.add("/" + path);
        for (String path: filesRaw) {
            this.paths.put(path.replaceFirst("/webServer", ""), path);
            if (path.endsWith("/index.html")) {
                this.paths.put(path.replaceFirst("/webServer", "").substring(0, path.length() - 20), path);
            }
        }
    }

    public ArrayList<String> getAllPaths() {
        ArrayList<String> ret = new ArrayList<>(paths.keySet().size());
        ret.addAll(paths.keySet());
        return ret;
    }

    public void onWebServer(HttpExchange httpExchange) {
        try {
            String fileName = paths.get(httpExchange.getRequestURI().getPath());
            if (fileName != null) {
                InputStream inputStream = getClass().getResourceAsStream(fileName);
                httpExchange.sendResponseHeaders(200, inputStream.available());
                OutputStream outputStream = httpExchange.getResponseBody();
                String mimeType = URLConnection.guessContentTypeFromName(fileName);
                if (mimeType != null) {
                    httpExchange.getResponseHeaders().set("Content-Type", mimeType + "; charset=utf-8");
                } else {
                    httpExchange.getResponseHeaders().set("Content-Type", "application/octet-stream; charset=utf-8");
                }

                int buffer = Disc0rd.getConfig().getWebServer().getBuffer();
                byte[] read = new byte[buffer];
                while (0 < (buffer = inputStream.read(read))) outputStream.write(read, 0, buffer);

                outputStream.close();
                inputStream.close();
                return;
            }
            httpExchange.sendResponseHeaders(404, 0);
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
    }

}
