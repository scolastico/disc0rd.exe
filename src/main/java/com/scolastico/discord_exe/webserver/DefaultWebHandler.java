package com.scolastico.discord_exe.webserver;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class DefaultWebHandler {

    private static DefaultWebHandler instance = null;
    private HashMap<String, String> paths = new HashMap<>();
    private ArrayList<String> filesRaw = new ArrayList<>();

    public static DefaultWebHandler getInstance() {
        if (instance == null) {
            instance = new DefaultWebHandler();
        }
        return instance;
    }

    private DefaultWebHandler() {
        getResourceFilesRecursive("/webServer/");
        for (String path:filesRaw) {
            this.paths.put(path.replaceFirst("/webServer", ""), path);
            if (path.endsWith("/index.html")) {
                this.paths.put(path.replaceFirst("/webServer", "").substring(0, path.length() - 20), path);
            }
        }
    }

    private void getResourceFilesRecursive(String path) {
        ArrayList<String> filePaths = getResourceFiles(path);
        for (String filePath:filePaths) {
            File file = new File(getClass().getResource(path + filePath).getPath());
            if (file.isDirectory()) {
                getResourceFilesRecursive(path + filePath + "/");
            } else {
                filesRaw.add(path + filePath);
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

    private ArrayList<String> getResourceFiles(String path) {
        try {
            ArrayList<String> filenames = new ArrayList<>();
            try (
                    InputStream in = getResourceAsStream(path);
                    BufferedReader br = new BufferedReader(new InputStreamReader(in))
            ) {
                String resource;
                while ((resource = br.readLine()) != null) {
                    filenames.add(resource);
                }
            }
            return filenames;
        } catch (Exception e) {
            ErrorHandler.getInstance().handleFatal(e);
        }
        return new ArrayList<>();
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in = getContextClassLoader().getResourceAsStream(resource);
        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

}
