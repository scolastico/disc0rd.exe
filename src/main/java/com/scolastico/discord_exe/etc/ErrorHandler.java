package com.scolastico.discord_exe.etc;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.config.ConfigHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class ErrorHandler {

    private Tools tools = Tools.getInstance();
    private static ErrorHandler instance = null;
    private int errorCount = 0;
    private HashMap<Long, String> errorLog = new HashMap<>();
    private ErrorHandler() {}
    public static ErrorHandler getInstance() {
        if (instance == null) {
            instance = new ErrorHandler();
        }
        return instance;
    }

    public HashMap<Long, String> getErrorLog() {
        return errorLog;
    }


    public void handle(Exception e) {
        if (Disc0rd.getConfig().getMaxErrorCountToShutDown() >= 0) errorCount++;
        tools.generateNewSpacesInConsole(1);
        addToErrorLog(e);
        outputErrorInfo(e);
        tools.generateNewSpacesInConsole(1);
        if (errorCount >= Disc0rd.getConfig().getMaxErrorCountToShutDown()) {
            handleFatal(new Exception("Max error count reached!"));
        }
    }

    public void handleFatal(Exception e) {
        if (tools.isShowingLoadingAnimation()) {
            System.out.println(" [FAIL]");
        }
        tools.generateNewSpacesInConsole(1);
        System.err.println("FATAL ERROR! SHUTTING DOWN!");
        addToErrorLog(e);
        outputErrorInfo(e);
        writeErrorLogToFile();
        tools.generateNewSpacesInConsole(1);
        Runtime.getRuntime().exit(0);
    }

    private void addToErrorLog(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String exceptionAsString = stringWriter.toString();
        errorLog.put((System.currentTimeMillis() / 1000L), exceptionAsString);
    }

    private void outputErrorInfo(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String exceptionAsString = stringWriter.toString();
        System.err.println("Message: " + e.getMessage());
        System.err.println("StackTrace:");
        System.err.println(exceptionAsString);
    }

    public void writeErrorLogToFile() {
        try {
            ConfigHandler configHandler = new ConfigHandler(errorLog, "error_log_" + Disc0rd.getStartTime() + ".log.json");
            configHandler.saveConfigObject();
        } catch (Exception ignore) {}
    }

}
