package com.scolastico.discord_exe.event.extendedEventSystem.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEvent;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventDataStore;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventManager;
import com.scolastico.discord_exe.event.extendedEventSystem.interfaces.Disc0rdEvent;
import com.scolastico.discord_exe.event.handlers.ScheduleHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;

@ScheduleHandler.ScheduleTime(tick = 1200)
public class OnLevelReached implements Disc0rdEvent, ScheduleHandler {
    HashMap<String, Integer> currentLevel = new HashMap<>();

    @Override
    public void registerDisc0rdEvent() {
        Disc0rd.getEventRegister().registerSchedule(this);
        scheduledTask();
    }

    @Override
    public String getName() {
        return "On Level Reached";
    }

    @Override
    public String getDescription() {
        return "Triggers if a user reaches a new level.\n" +
                "The level which is reached 'level',\n" +
                "the user name is saved in 'user-name',\n" +
                "the user id is saved in 'user-id'.";
    }

    @Override
    public HashMap<String, String> getConfig() {
        return new HashMap<String, String>();
    }

    @Override
    public void scheduledTask() {
        HashMap<Long, ServerSettings> allServerSettings = Disc0rd.getMysql().getAllServerSettings();
        for (Long guildId:allServerSettings.keySet()) {
            ServerSettings settings = allServerSettings.get(guildId);
            for (long id:settings.getLeaderboard().getUsers().keySet()) {
                long xp = settings.getLeaderboard().getUserXP(id);
                int level = Tools.getInstance().getLeaderboardLevel(xp);
                User user = Disc0rd.getJda().getUserById(id);
                if (user != null) {
                    if (currentLevel.containsKey(guildId + "-" + id)) {
                        if (!currentLevel.get(guildId + "-" + id).equals(level)) {
                            currentLevel.remove(guildId + "-" + id);
                            currentLevel.put(guildId + "-" + id, level);
                            for (ExtendedEvent extendedEvent : ExtendedEventManager.getInstance().getExtendedEvents(guildId)) {
                                if (extendedEvent.getEvent().equals(getName())) {
                                    ExtendedEventDataStore dataStore = new ExtendedEventDataStore(extendedEvent);
                                    dataStore.setDataStore("level", Integer.toString(level));
                                    dataStore.setDataStore("user-name", user.getName());
                                    dataStore.setDataStore("user-id", Long.toString(id));
                                    ExtendedEventManager.getInstance().executeAction(dataStore);
                                }
                            }
                        }
                    } else {
                        currentLevel.put(guildId + "-" + id, level);
                    }
                }
            }
        }
    }
}
