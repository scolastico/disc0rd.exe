package com.scolastico.discord_exe.event.extendedEventSystem.actions;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.ScheduleTask;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventDataStore;
import com.scolastico.discord_exe.event.extendedEventSystem.interfaces.Disc0rdAction;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import net.dv8tion.jda.api.entities.Guild;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendWebhook implements Disc0rdAction, EventHandler {
    private static HashMap<Long, Integer> limitsLog = new HashMap<>();

    @Override
    public ExtendedEventDataStore doAction(ExtendedEventDataStore dataStore, Integer idFromAction) {
        try {
            HashMap<String, String> config = dataStore.getExtendedEvent().getActions().get(idFromAction).getConfig();
            Guild guild = Disc0rd.getJda().getGuildById(dataStore.getExtendedEvent().getGuild());
            if (guild == null) {
                Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[WebHook Request (POST)] [" + idFromAction + "] Guild not found!");
                dataStore.setCancelled(true);
                return dataStore;
            }
            String urlString = Tools.getInstance().getStringWithVarsFromDataStore(dataStore, config.getOrDefault("URL", ""));
            if (config.get("URL").equals("")) {
                Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[WebHook Request (POST)] [" + idFromAction + "] URL is not set!");
                dataStore.setCancelled(true);
                return dataStore;
            }
            URL url = new URL(urlString);
            Pattern pattern = Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(url.getHost());
            if (matcher.find()) {
                Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[WebHook Request (POST)] [" + idFromAction + "] IP's are not allowed!");
                dataStore.setCancelled(true);
                return dataStore;
            }
            ServerSettings.ServerLimits limits = Tools.getInstance().getLimitFromGuild(dataStore.getExtendedEvent().getGuild());
            if (limitsLog.getOrDefault(dataStore.getExtendedEvent().getGuild(), 0) >= limits.getPerMinuteOutgoingWebHookCalls()) {
                Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[WebHook Request (POST)] [" + idFromAction + "] Limit reached!");
                dataStore.setCancelled(true);
                return dataStore;
            } else {
                if (limitsLog.containsKey(dataStore.getExtendedEvent().getGuild())) {
                    limitsLog.put(dataStore.getExtendedEvent().getGuild(), limitsLog.get(dataStore.getExtendedEvent().getGuild()) + 1);
                } else {
                    limitsLog.put(dataStore.getExtendedEvent().getGuild(), 1);
                }
            }
            if (urlString.startsWith("https://")) {
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Disc0rd.exe - Free discord tool bot");
                con.setConnectTimeout(1000 * 5);
                con.setReadTimeout(1000 * 5);
                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                dataStore.setDataStore("action-" + idFromAction + "-code", Integer.toString(responseCode));
                dataStore.setDataStore("action-" + idFromAction + "-response", content.toString());
            } else if (urlString.startsWith("http://")) {
                HttpURLConnection con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Disc0rd.exe - Free discord tool bot");
                con.setConnectTimeout(1000 * 5);
                con.setReadTimeout(1000 * 5);
                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                dataStore.setDataStore("action-" + idFromAction + "-code", Integer.toString(responseCode));
                dataStore.setDataStore("action-" + idFromAction + "-response", content.toString());
            } else {
                Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[WebHook Request (POST)] [" + idFromAction + "] Only http and https is supported!");
                dataStore.setCancelled(true);
                return dataStore;
            }
        } catch(MalformedURLException ignored) {
            Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[WebHook Request (POST)] [" + idFromAction + "] URL could not be parsed!");
            dataStore.setCancelled(true);
            return dataStore;
        } catch(FileNotFoundException ignored) {
            Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[WebHook Request (POST)] [" + idFromAction + "] URL not found! (404)");
            dataStore.setCancelled(true);
            return dataStore;
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
            Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[WebHook Request (POST)] [" + idFromAction + "] Internal server error while trying to send webhook! The error log is automatically reported to the dev team!");
            dataStore.setCancelled(true);
            return dataStore;
        }
        return dataStore;
    }

    @Override
    public String getName() {
        return "Send WebHook Request (POST)";
    }

    @Override
    public String getDescription() {
        return "Send a webhook request via POST and 'application/x-www-form-urlencoded'.\n\n" +
                "The response code is saved in 'action-{id}-code'\n" +
                "and the response is saved in 'action-{id}-response'.";
    }

    @Override
    public HashMap<String, String> getConfig() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("URL", "The URL to be called.");
        ret.put("Post Values", "The POST values that should be given with the request. In the format 'application/x-www-form-urlencoded'. Example: 'key1=value1&key2=value2'");
        return ret;
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        ScheduleTask.getInstance().runScheduledTaskRepeat(() -> limitsLog = new HashMap<>(), 60*20, 60*20, false);
    }
}
