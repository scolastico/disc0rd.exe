package com.scolastico.discord_exe.etc;

import java.util.concurrent.TimeUnit;

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

    private Boolean _isShowingLoadingAnimation = false;
    public Boolean isShowingLoadingAnimation() {
        return _isShowingLoadingAnimation;
    }

    public void asyncLoadingAnimationWhileWaitingResult(Runnable function) {
        _isShowingLoadingAnimation = true;
        Thread thread = new Thread(function);
        thread.start();
        while(thread.isAlive()) {
            System.out.print(".");
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException ignored) {}
        }
        System.out.println(" [OK]");
        _isShowingLoadingAnimation = false;
    }

}
