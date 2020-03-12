package com.scolastico.discord_exe.event.extendedEventSystem.interfaces;

import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventDataStore;

import java.util.HashMap;

public interface Disc0rdAction {

    public ExtendedEventDataStore doAction(ExtendedEventDataStore dataStore, Integer idFromAction);

    public String getName();
    public String getDescription();
    public HashMap<String, String> getConfig();

}
