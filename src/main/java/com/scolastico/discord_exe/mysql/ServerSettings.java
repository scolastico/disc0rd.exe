package com.scolastico.discord_exe.mysql;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerSettings {

    private boolean isSpecialServer = false;

    private HashMap<String, String> shortCuts = new HashMap<String, String>();

    public boolean isSpecialServer() {
        return isSpecialServer;
    }

    public void setSpecialServer(boolean specialServer) {
        isSpecialServer = specialServer;
    }

    public HashMap<String, String> getShortCuts() {
        return shortCuts;
    }

    public void setShortCuts(HashMap<String, String> shortCuts) {
        this.shortCuts = shortCuts;
    }
}
