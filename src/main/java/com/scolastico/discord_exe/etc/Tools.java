package com.scolastico.discord_exe.etc;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.config.ConfigDataStore;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventDataStore;
import com.scolastico.discord_exe.mysql.ServerSettings;
import com.sun.net.httpserver.HttpExchange;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Tools {

    private Boolean _isShowingLoadingAnimation = false;
    private static Tools instance = null;
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    private Tools() {
        Disc0rd.addOnExitRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    ErrorHandler.getInstance().handle(e);
                }
            }
        });
    }
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
        int counter = 50;
        while(thread.isAlive()) {
            if (counter >= 50) {
                System.out.print(".");
                counter = 0;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(10);
                counter++;
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
            if (!httpExchange.getRequestHeaders().containsKey("Content-Type")) return hashMap;
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
                    hashMap.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8.toString()));
                }
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return hashMap;
    }

    public HashMap<String, String> getGetValuesFromHttpExchange(HttpExchange httpExchange) {
        HashMap<String, String> result = new HashMap<>();
        try {
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) return result;
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], entry[1]);
                }else{
                    result.put(entry[0], "");
                }
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return result;
    }

    public JSONObject getJsonFromHttpExchange(HttpExchange httpExchange) {
        JSONObject jsonObject = null;
        try {
            if (httpExchange.getRequestHeaders().containsKey("content-type")) {
                if (httpExchange.getRequestHeaders().getFirst("content-type").equalsIgnoreCase("application/json")) {
                    BufferedReader httpInput = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8));
                    StringBuilder in = new StringBuilder();
                    String input;
                    while ((input = httpInput.readLine()) != null) {
                        in.append(input).append(" ");
                    }
                    httpInput.close();
                    input = in.toString();
                    jsonObject = new JSONObject(input);
                }
            }
        } catch (JSONException ignored) {
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return jsonObject;
    }

    public String getJsonStringFromHttpExchange(HttpExchange httpExchange) {
        try {
            if (httpExchange.getRequestHeaders().containsKey("content-type")) {
                if (httpExchange.getRequestHeaders().getFirst("content-type").equalsIgnoreCase("application/json")) {
                    BufferedReader httpInput = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8));
                    StringBuilder in = new StringBuilder();
                    String input;
                    while ((input = httpInput.readLine()) != null) {
                        in.append(input).append(" ");
                    }
                    httpInput.close();
                    input = in.toString();
                    new JSONObject(input);
                    return input;
                }
            }
        } catch (JSONException ignored) {
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return null;
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

    public String sendPostRequest(String uri) {
        try {
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return content.toString();
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return null;
    }

    public String sendPostRequest(String uri, String json) {
        try {
            URL url = new URL(uri);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            byte[] input = json.getBytes(StandardCharsets.UTF_8.name());
            OutputStream os = con.getOutputStream();
            os.write(input, 0, input.length);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return content.toString();
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return null;
    }

    public HashMap<Integer, HashMap<String, String>> splitToSites(HashMap<String, String> map) {
        return splitToSites(map, 25);
    }

    public HashMap<Integer, HashMap<String, String>> splitToSites(HashMap<String, String> map, Integer maxSiteValues) {
        HashMap<Integer, HashMap<String, String>> ret = new HashMap<>();
        HashMap<String, String> nextEntry = new HashMap<>();
        int site = 1;
        int tmp = 1;
        for (String key:map.keySet()) {
            nextEntry.put(key, map.get(key));
            if (tmp == maxSiteValues) {
                ret.put(site, nextEntry);
                site++;
                nextEntry = new HashMap<>();
                tmp = 0;
            }
            tmp++;
        }
        ret.put(site, nextEntry);
        return ret;
    }

    public int tryToParseInt(String integer) {
        try {
            return Integer.parseInt(integer);
        } catch (Exception ignored) {}
        return 0;
    }

    public void writeGuildLogLine(long guildId, String logText) {
        ServerSettings serverSettings = Disc0rd.getMysql().getServerSettings(guildId);
        String log = serverSettings.getLog();
        while (countLines(log) >= getLimitFromGuild(guildId).getLogLines()) {
            log = log.substring(0, log.lastIndexOf("\n"));
            if (log.equals("")) break;
        }
        log = "[" + new Date().toString() + "] " + logText + "\n" + log;
        serverSettings.setLog(log);
    }

    public int countLines(String str){
        String[] lines = str.split("\n");
        return  lines.length;
    }

    public String getStringWithVarsFromDataStore(ExtendedEventDataStore dataStore, String string) {
        for (String key:dataStore.getDataStore().keySet()) {
            string = string.replace("{" + key + "}", dataStore.getDataStore().get(key));
        }
        return string;
    }

    public ServerSettings.ServerLimits getLimitFromGuild(long guildId) {
        ServerSettings.ServerLimits serverLimits = Disc0rd.getMysql().getServerSettings(guildId).getServerLimits();
        ConfigDataStore.DefaultLimits defaultLimits = Disc0rd.getConfig().getDefaultLimits();
        if (serverLimits.getActionsPerEvent() == 0) serverLimits.setActionsPerEvent(defaultLimits.getActionsPerEvent());
        if (serverLimits.getEvents() == 0) serverLimits.setEvents(defaultLimits.getEvents());
        if (serverLimits.getLogLines() == 0) serverLimits.setLogLines(defaultLimits.getLogLines());
        if (serverLimits.getPermissions() == 0) serverLimits.setPermissions(defaultLimits.getPermissions());
        if (serverLimits.getPerMinuteWebHookCalls() == 0) serverLimits.setPerMinuteWebHookCalls(defaultLimits.getPerMinuteWebHookCalls());
        if (serverLimits.getPerMinuteOutgoingWebHookCalls()== 0) serverLimits.setPerMinuteOutgoingWebHookCalls(defaultLimits.getPerMinuteOutgoingWebHookCalls());
        return serverLimits;
    }

    public String getStringFromFileInternally(String path) {
        String ret = "";
        try {
            InputStream stream = getClass().getResourceAsStream("/stringDataStore/" + path);
            ret = IOUtils.toString(stream, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return ret;
    }

    public int getLeaderboardLevel(long xp) {
        long currentXp = 100;
        int ret = 0;
        while (currentXp <= xp) {
            currentXp = (currentXp*250L)/100L;
            ret++;
        }
        return ret;
    }

    public long getLeaderboardNextLevelXP(long xp) {
        return getLeaderboardLevelXP(getLeaderboardLevel(xp)+1);
    }

    public long getLeaderboardLevelXP(int level) {
        long ret = 0;
        for (int index = 0; index != level; index++) {
            if (ret == 0) {
                ret = 100;
            } else {
                ret = (ret*250L)/100L;
            }
        }
        return ret;
    }

    public int getLeaderboardPlace(Guild guild, Member member) {
        return getLeaderboardPlace(guild.getIdLong(), member.getIdLong());
    }

    public int getLeaderboardPlace(long guildId, long userId) {
        HashMap<Long, Long> leaderboard = Disc0rd.getMysql().getServerSettings(guildId).getLeaderboard().getUsers();
        if (!leaderboard.containsKey(userId)) return 0;
        long xp = leaderboard.get(userId);
        int place = 1;
        for (long id:leaderboard.keySet()) {
            if (id != userId) {
                if (leaderboard.get(id) > xp) place++;
            }
        }
        return place;
    }

    public String getLeaderboardBannerSVG(Guild guild, Member member) {
        String base64image = "";
        try {
            String avatarUrl = member.getUser().getAvatarUrl();
            if (avatarUrl != null) {
                HttpGet request = new HttpGet(avatarUrl);
                request.addHeader(HttpHeaders.USER_AGENT, "Java Discord Bot - Disc0rd.exe");
                CloseableHttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                byte[] fileContent = EntityUtils.toByteArray(entity);
                base64image = "data:image/png;base64," + Base64.getEncoder().encodeToString(fileContent);
            }
        } catch (Exception ignored) {}
        ServerSettings settings = Disc0rd.getMysql().getServerSettings(guild.getIdLong());
        ServerSettings.Leaderboard leaderboard = settings.getLeaderboard();
        long xp = leaderboard.getUserXP(member.getIdLong());
        int currentLevel = Tools.getInstance().getLeaderboardLevel(xp);
        long currentLevelXp = Tools.getInstance().getLeaderboardLevelXP(currentLevel);
        long nextLevelXp = Tools.getInstance().getLeaderboardLevelXP(currentLevel+1);
        long levelDifference = nextLevelXp-currentLevelXp;
        long currentXpFromLevel = xp-currentLevelXp;
        String banner = Tools.getInstance().getStringFromFileInternally("leaderboard/banner.svg");
        if (!base64image.equals("")) banner = banner.replaceAll("%image%", "<image x=\"21\" y=\"31\" width=\"80\" height=\"80\" clip-path=\"url(#clipCircle)\" xlink:href=\"" + base64image + "\"></image>");
        banner = banner.replaceAll("%username%", member.getUser().getName());
        banner = banner.replaceAll("%tag%", member.getUser().getAsTag().split("#")[1]);
        banner = banner.replaceAll("%currentXP%", Long.toString(xp-currentLevelXp));
        banner = banner.replaceAll("%nextLevelXP%", Long.toString(nextLevelXp-currentLevelXp));
        banner = banner.replaceAll("%level%", Integer.toString(currentLevel));
        banner = banner.replaceAll("%percentage%", Double.toString(((((double)currentXpFromLevel/(double)levelDifference)*100)*315)/100));
        return banner;
    }

    public String[] splitSpring(String input, int maxLength) {
        String[] splicedString = input.split(" ");
        StringBuilder output = new StringBuilder();
        ArrayList<String> list = new ArrayList<>();
        for (int tmp = 0;tmp != splicedString.length;tmp++) {
            String word = splicedString[tmp];
            if ((output.length() + word.length()) >= maxLength) {
                list.add(output.toString());
                output = new StringBuilder();
            }
            output.append(word).append(" ");
        }
        if (output.length() != 0) list.add(output.toString());
        return list.toArray(new String[0]);
    }

    public Long getUnixTimeStamp() {
        return System.currentTimeMillis() / 1000L;
    }

}
