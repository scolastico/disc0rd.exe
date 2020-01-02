package com.scolastico.discord_exe.mysql;

import java.util.ArrayList;
import java.util.HashMap;

public class ServerSettings {

    private boolean isSpecialServer = false;

    private ArrayList<HashMap<String, String>> shortCuts = new ArrayList<HashMap<String, String>>();

    public boolean isSpecialServer() {
        return isSpecialServer;
    }

    public void setSpecialServer(boolean specialServer) {
        isSpecialServer = specialServer;
    }

    public ArrayList<HashMap<String, String>> getShortCuts() {
        return shortCuts;
    }

    public void setShortCuts(ArrayList<HashMap<String, String>> shortCuts) {
        this.shortCuts = shortCuts;
    }
}
