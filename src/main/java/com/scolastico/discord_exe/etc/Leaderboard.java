package com.scolastico.discord_exe.etc;

public class Leaderboard {

    private static Leaderboard instance = null;

    private Leaderboard() {}
    public static Leaderboard getInstance() {
        if (instance == null) {
            instance = new Leaderboard();
        }
        return instance;
    }



}
