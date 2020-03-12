package com.scolastico.discord_exe.event.extendedEventSystem;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.extendedEventSystem.interfaces.Disc0rdAction;
import com.scolastico.discord_exe.event.extendedEventSystem.interfaces.Disc0rdEvent;
import com.scolastico.discord_exe.mysql.ServerSettings;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ExtendedEventManager {

    private static ExtendedEventManager instance = null;

    private ArrayList<ExtendedEvent> extendedEvents = new ArrayList<>();
    private HashMap<String, Disc0rdAction> actions = new HashMap<>();
    private HashMap<String, Disc0rdEvent> events = new HashMap<>();
    private boolean isReloading = false;

    private ExtendedEventManager() {
        try {
            Reflections reflections = new Reflections("com.scolastico.discord_exe");
            Set<Class<? extends Disc0rdAction>> disc0rdActions = reflections.getSubTypesOf(Disc0rdAction.class);
            for (Class<? extends Disc0rdAction> actionClass:disc0rdActions) {
                Disc0rdAction action = actionClass.newInstance();
                actions.put(action.getName(), action);
            }
            Set<Class<? extends Disc0rdEvent>> disc0rdEvents = reflections.getSubTypesOf(Disc0rdEvent.class);
            for (Class<? extends Disc0rdEvent> eventClass:disc0rdEvents) {
                Disc0rdEvent event = eventClass.newInstance();
                event.registerDisc0rdEvent();
                events.put(event.getName(), event);
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handleFatal(e);
            return;
        }
        reloadExtendedEvents();
    }

    public static ExtendedEventManager getInstance() {
        if (instance == null) {
            instance = new ExtendedEventManager();
        }
        return instance;
    }

    public HashMap<String, Disc0rdAction> getActions() {
        return actions;
    }

    public HashMap<String, Disc0rdEvent> getEvents() {
        return events;
    }

    public void reloadExtendedEvents() {
        isReloading = true;
        extendedEvents = new ArrayList<>();
        ArrayList<ServerSettings> serverSettings = Disc0rd.getMysql().getAllServerSettings();
        for (ServerSettings serverSetting:serverSettings) {
            extendedEvents.addAll(serverSetting.getExtendedEvents());
        }
        isReloading = false;
    }

    public void executeAction(ExtendedEventDataStore dataStore) {
        ExtendedEvent extendedEvent = dataStore.getExtendedEvent();
        try {
            ArrayList<Integer> ids = new ArrayList<>(extendedEvent.getActions().keySet());
            Collections.sort(ids);
            for (Integer id:ids) {
                Disc0rdAction action = actions.get(extendedEvent.getActions().get(id).getAction());
                dataStore = action.doAction(dataStore, id);
                if (dataStore.isCancelled()) break;
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
            Tools.getInstance().writeGuildLogLine(extendedEvent.getGuild(), "[INTERNAL] [-1] An internal error Occurred! You dont need to inform the bot owner! An information about this event is automatically send!");
        }
    }

    public void setExtendedEvent(ExtendedEvent extendedEvent) {
        ExtendedEvent toDelete = null;
        for (ExtendedEvent event:extendedEvents) {
            if (event.getGuild().equals(extendedEvent.getGuild()) && event.getName().equals(extendedEvent.getName())) {
                toDelete = event;
            }
        }
        if (toDelete != null) {
            deleteExtendedEvent(toDelete);
        }
        extendedEvents.add(extendedEvent);
        ServerSettings settings = Disc0rd.getMysql().getServerSettings(extendedEvent.getGuild());
        ArrayList<ExtendedEvent> extendedEvents = settings.getExtendedEvents();
        extendedEvents.add(extendedEvent);
        settings.setExtendedEvents(extendedEvents);
        Disc0rd.getMysql().setServerSettings(extendedEvent.getGuild(), settings);
    }

    public void deleteExtendedEvent(ExtendedEvent extendedEvent) {
        extendedEvents.remove(extendedEvent);
        ServerSettings settings = Disc0rd.getMysql().getServerSettings(extendedEvent.getGuild());
        ArrayList<ExtendedEvent> extendedEvents = settings.getExtendedEvents();
        ArrayList<ExtendedEvent> toDelete = new ArrayList<>();
        for (ExtendedEvent event:extendedEvents) {
            if (event.getName().equals(extendedEvent.getName())) toDelete.add(event);
        }
        for (ExtendedEvent event:toDelete) {
            extendedEvents.remove(event);
        }
        settings.setExtendedEvents(extendedEvents);
        Disc0rd.getMysql().setServerSettings(extendedEvent.getGuild(), settings);
    }

    public ExtendedEvent getExtendedEvent(long guildId, String name) {
        for (ExtendedEvent event:extendedEvents) {
            if (event.getGuild() == guildId && event.getName().equals(name)) return event;
        }
        return null;
    }

    public ArrayList<ExtendedEvent> getExtendedEvents(long guildId) {
        ArrayList<ExtendedEvent> ret = new ArrayList<>();
        for (ExtendedEvent event:extendedEvents) {
            if (event.getGuild() == guildId) ret.add(event);
        }
        return ret;
    }

    public ArrayList<ExtendedEvent> getExtendedEvents() {
        try {
            while (isReloading) TimeUnit.MILLISECONDS.sleep(10);
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return extendedEvents;
    }

    public void setExtendedEvents(ArrayList<ExtendedEvent> extendedEvents) {
        this.extendedEvents = extendedEvents;
    }

}
