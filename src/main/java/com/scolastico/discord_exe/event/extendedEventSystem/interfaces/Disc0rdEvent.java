package com.scolastico.discord_exe.event.extendedEventSystem.interfaces;

import java.util.HashMap;

public interface Disc0rdEvent {

    public void registerDisc0rdEvent();

    public String getName();
    public String getDescription();
    public HashMap<String, String> getConfig();

}
