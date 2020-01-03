package com.scolastico.discord_exe;

import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.config.ConfigDataStore;
import com.scolastico.discord_exe.config.ConfigHandler;
import com.scolastico.discord_exe.etc.*;
import com.scolastico.discord_exe.event.events.CommandHelp;
import com.scolastico.discord_exe.event.events.EventHandler;
import com.scolastico.discord_exe.mysql.MysqlHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.reflections.Reflections;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Set;

public class Disc0rd {

    private static Tools tools = Tools.getInstance();
    private static ConfigHandler configHandler;
    private static ConfigDataStore config;
    private static MysqlHandler mysql;
    private static JDA jda;
    private static boolean ready = false;
    private static String version = "Can't read Version! This build is corrupt!";

    public static String getVersion() {
        return version;
    }

    public static boolean isReady() {
        return ready;
    }

    public static MysqlHandler getMysql() {
        return mysql;
    }

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        try {
            version = VersionController.getVersion() + "-" + VersionController.getCommit();
        } catch (Exception ignored) {}

        System.out.println("  _____  _           ___          _                ");
        System.out.println(" |  __ \\(_)         / _ \\        | |               ");
        System.out.println(" | |  | |_ ___  ___| | | |_ __ __| |  _____  _____ ");
        System.out.println(" | |  | | / __|/ __| | | | '__/ _\\ | / _ \\ \\/ / _ \\");
        System.out.println(" | |__| | \\__ \\ (__| |_| | | | (_| ||  __/>  <  __/");
        System.out.println(" |_____/|_|___/\\___|\\___/|_|  \\__,_(_)___/_/\\_\\___|");
        System.out.println(" --------------------------------------------------");
        System.out.println("Disc0rd.exe | by scolastico | Version: " + version);

        tools.generateNewSpacesInConsole(1);

        System.out.print("Loading configuration module ");
        tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
            public void run() {
                try {
                    configHandler = new ConfigHandler(new ConfigDataStore(), "config.json", true);
                    Object obj = configHandler.getConfigObject();
                    if (obj instanceof ConfigDataStore) {
                        config = (ConfigDataStore) obj;
                    } else {
                        throw new Exception("Config not valid! Please delete you config and try again!");
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().handleFatal(e);
                }
            }
        });

        System.out.print("Loading MySQL module ");
        tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
            public void run() {
                try {
                    mysql = new MysqlHandler(config.getMysql_server(), config.getMysql_user(), config.getMysql_pass(), config.getMysql_database(), config.getMysql_prefix());
                } catch (Exception e) {
                    ErrorHandler.getInstance().handleFatal(e);
                }
            }
        });

        System.out.print("Loading java discord api module ");
        tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
            public void run() {
                try {
                    JDABuilder builder = new JDABuilder(config.getDiscord_token());
                    builder.setAutoReconnect(true);
                    jda = builder.build().awaitReady();
                } catch (LoginException e) {
                    ErrorHandler.getInstance().handleFatal(e);
                } catch (InterruptedException e) {
                    ErrorHandler.getInstance().handleFatal(e);
                }
            }
        });

        System.out.print("Loading event module ");
        tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
            public void run() {
                try {
                    EventRegister eventRegister = EventRegister.getInstance();

                    Reflections reflections = new Reflections("com.scolastico.discord_exe");
                    Set<Class<? extends EventHandler>> eventHandlers = reflections.getSubTypesOf(EventHandler.class);
                    for (Class<?> eventHandler:eventHandlers) {
                        Object obj = eventHandler.newInstance();
                        if (obj instanceof EventHandler) {
                            ((EventHandler) obj).registerEvents(eventRegister);
                        }
                    }

                    jda.addEventListener(eventRegister);
                } catch (Exception e) {
                    ErrorHandler.getInstance().handleFatal(e);
                }
            }
        });

        tools.generateNewSpacesInConsole(1);
        System.out.println("Loading Finished! Took " + (System.currentTimeMillis() - startTime) + " ms!");

        ready = true;

    }

}
