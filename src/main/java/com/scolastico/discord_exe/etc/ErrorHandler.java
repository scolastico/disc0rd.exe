package com.scolastico.discord_exe.etc;

public class ErrorHandler {

    private Tools tools = Tools.getInstance();

    private ErrorHandler() {}
    private static ErrorHandler instance = null;
    public static ErrorHandler getInstance() {
        if (instance == null) {
            instance = new ErrorHandler();
        }
        return instance;
    }

    public void handle(Exception e) {
        tools.generateNewSpacesInConsole(5);

    }

    public void handleFatal(Exception e) {
        tools.generateNewSpacesInConsole(5);
        System.out.println();
        handle(e);
    }

}
