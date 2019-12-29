package com.scolastico.discord_exe.etc;

public class Tools {

    private Tools() {}
    private static Tools instance = null;
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

}
