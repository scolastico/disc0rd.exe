package com.scolastico.discord_exe.etc;

import com.sun.net.httpserver.HttpExchange;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Tools {

    private Boolean _isShowingLoadingAnimation = false;
    private static Tools instance = null;

    private Tools() {}
    public static Tools getInstance() {
        if (instance == null) {
            instance = new Tools();
        }
        return instance;
    }

    public void generateNewSpacesInConsole(int times) {
        for (int tmp = 0; times > tmp; tmp++) {
            System.out.println("");
        }
    }

    public Boolean isShowingLoadingAnimation() {
        return _isShowingLoadingAnimation;
    }

    public void asyncLoadingAnimationWhileWaitingResult(Runnable function) {
        _isShowingLoadingAnimation = true;
        Thread thread = new Thread(function);
        thread.start();
        while(thread.isAlive()) {
            System.out.print(".");
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException ignored) {}
        }
        System.out.println(" [OK]");
        _isShowingLoadingAnimation = false;
    }

    public boolean isOwner(Guild guild, User user) {
        return guild.getOwnerId().equals(user.getId());
    }

    public String getAlphaNumericString(int length) {
        String AlphaNumericString = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            stringBuilder.append(AlphaNumericString.charAt(index));
        }
        return stringBuilder.toString();
    }

    public HashMap<String, String> getPostValuesFromHttpExchange(HttpExchange httpExchange) {
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            if (!httpExchange.getRequestHeaders().getFirst("Content-Type").equals("application/x-www-form-urlencoded")) return hashMap;
            StringBuilder stringBuilder = new StringBuilder();
            InputStream inputStream = httpExchange.getRequestBody();
            int i;
            while ((i = inputStream.read()) != -1) {
                stringBuilder.append((char) i);
            }
            for (String pair:stringBuilder.toString().split("&")) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    hashMap.put(keyValue[0], keyValue[1]);
                } else {
                    ErrorHandler.getInstance().handle(new Exception("Error while parsing post values: '" + stringBuilder.toString() + "'"));
                }
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return hashMap;
    }

    public Color hex2Rgb(String colorStr) {
        return new Color(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    }

    public String rgb2Hex(Color color) {
        return "#" + Integer.toHexString(color.getRGB()).substring(2);
    }

    public boolean isColorSimilar(Color colorBase, Color color, int sensitivity) {
        for (int r = (-sensitivity); sensitivity >= r; r++) {
            for (int g = (-sensitivity); sensitivity >= g; g++) {
                for (int b = (-sensitivity); sensitivity >= b; b++) {
                    if ((colorBase.getRed() - r) == color.getRed() && (colorBase.getGreen() - g) == color.getGreen() && (colorBase.getBlue() - b) == color.getBlue()) return true;
                }
            }
        }
        return false;
    }

}
