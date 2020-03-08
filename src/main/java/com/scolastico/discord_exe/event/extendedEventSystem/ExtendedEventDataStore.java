package com.scolastico.discord_exe.event.extendedEventSystem;

import java.util.HashMap;

public class ExtendedEventDataStore {

    private ExtendedEvent extendedEvent;
    private HashMap<String, String> dataStore = new HashMap<>();
    private boolean cancelled = false;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public ExtendedEventDataStore(ExtendedEvent extendedEvent) {
        this.extendedEvent = extendedEvent;
    }

    public ExtendedEvent getExtendedEvent() {
        return extendedEvent;
    }

    public void setExtendedEvent(ExtendedEvent extendedEvent) {
        this.extendedEvent = extendedEvent;
    }

    public HashMap<String, String> getDataStore() {
        return dataStore;
    }

    public void setDataStore(HashMap<String, String> dataStore) {
        this.dataStore = dataStore;
    }

    public String getDataStore(String path) {
        return dataStore.getOrDefault(path, "");
    }

    public void setDataStore(String path, String value) {
        dataStore.remove(path);
        dataStore.put(path, value);
    }

}
