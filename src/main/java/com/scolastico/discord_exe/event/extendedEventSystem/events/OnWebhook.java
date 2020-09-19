package com.scolastico.discord_exe.event.extendedEventSystem.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.ScheduleTask;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEvent;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventDataStore;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventManager;
import com.scolastico.discord_exe.event.extendedEventSystem.interfaces.Disc0rdEvent;
import com.scolastico.discord_exe.mysql.ServerSettings;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;

public class OnWebhook implements Disc0rdEvent {
    private static HashMap<Long, Integer> limitsLog = new HashMap<>();

    public static void writeRequest(HttpExchange exchange, String guildId, String path) {
        for (ExtendedEvent extendedEvent:ExtendedEventManager.getInstance().getExtendedEvents()) {
            try {
                if (extendedEvent.getEvent().equals("On Webhook (POST)")) {
                    if (extendedEvent.getGuild().toString().equals(guildId)) {
                        if (extendedEvent.getEventConfig().containsKey("URL") && extendedEvent.getEventConfig().get("URL").equals(path)) {
                            ServerSettings.ServerLimits limits = Tools.getInstance().getLimitFromGuild(extendedEvent.getGuild());
                            if (limitsLog.getOrDefault(extendedEvent.getGuild(), 0) >= limits.getPerMinuteOutgoingWebHookCalls()) {
                                Tools.getInstance().writeGuildLogLine(extendedEvent.getGuild(), "[On Webhook (POST)] Limit reached!");
                                return;
                            } else {
                                if (limitsLog.containsKey(extendedEvent.getGuild())) {
                                    limitsLog.put(extendedEvent.getGuild(), limitsLog.get(extendedEvent.getGuild()) + 1);
                                } else {
                                    limitsLog.put(extendedEvent.getGuild(), 1);
                                }
                            }
                            ExtendedEventDataStore dataStore = new ExtendedEventDataStore(extendedEvent);
                            HashMap<String, String> postValues = Tools.getInstance().getPostValuesFromHttpExchange(exchange);
                            if (postValues != null) {
                                for (String key:postValues.keySet()) {
                                    String value = postValues.get(key);
                                    dataStore.setDataStore("event-post-" + key, value);
                                }
                            }
                            ExtendedEventManager.getInstance().executeAction(dataStore);
                        } else {
                            Tools.getInstance().writeGuildLogLine(extendedEvent.getGuild(), "[On Webhook (POST)] URL cant be empty.");
                        }
                    }
                }
            } catch (Exception e) {
                ErrorHandler.getInstance().handle(e);
            }
        }
    }

    @Override
    public void registerDisc0rdEvent() {
        ScheduleTask.getInstance().runScheduledTaskRepeat(() -> limitsLog = new HashMap<>(), 60*20, 60*20, false);
    }

    @Override
    public String getName() {
        return "On Webhook (POST)";
    }

    @Override
    public String getDescription() {
        return "This event is triggered if you send a web request to a predefined url.\n\n" +
                "The POST values are saved in 'event-post-<value name>'.";
    }

    @Override
    public HashMap<String, String> getConfig() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("URL", "You can define here your path ending.\nYour URL will be then: " + Disc0rd.getConfig().getWebServer().getDomain() + "api/v1/webhook/<your guild id here>/<the url ending you defined here>\n\nIt is recommended to use some random string to secure your api.");
        return ret;
    }

}
