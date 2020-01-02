package com.scolastico.discord_exe.etc;

import java.io.PrintWriter;
import java.io.StringWriter;

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
        tools.generateNewSpacesInConsole(1);
        outputErrorInfo(e);
        tools.generateNewSpacesInConsole(1);
    }

    public void handleFatal(Exception e) {
        if (tools.isShowingLoadingAnimation()) {
            System.out.println(" [FAIL]");
        }
        tools.generateNewSpacesInConsole(1);
        System.err.println("FATAL ERROR! SHUTTING DOWN!");
        outputErrorInfo(e);
        tools.generateNewSpacesInConsole(1);
        System.exit(1);
    }

    private void outputErrorInfo(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String exceptionAsString = stringWriter.toString();
        System.err.println("Message: " + e.getMessage());
        System.err.println("StackTrace:");
        System.err.println(exceptionAsString);
    }

}
