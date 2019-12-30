package com.scolastico.discord_exe;

import com.scolastico.discord_exe.config.ConfigDataStore;
import com.scolastico.discord_exe.config.ConfigHandler;
import com.scolastico.discord_exe.etc.CommandModule;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.mysql.MysqlHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Disc0rd {

    private static Tools tools = Tools.getInstance();
    private static ConfigHandler configHandler;
    private static ConfigDataStore config;
    private static MysqlHandler mysql;
    private static JDA jda;

    public static void main(String[] args) {

        System.out.println("  _____  _           ___          _                ");
        System.out.println(" |  __ \\(_)         / _ \\        | |               ");
        System.out.println(" | |  | |_ ___  ___| | | |_ __ __| |  _____  _____ ");
        System.out.println(" | |  | | / __|/ __| | | | '__/ _\\ | / _ \\ \\/ / _ \\");
        System.out.println(" | |__| | \\__ \\ (__| |_| | | | (_| ||  __/>  <  __/");
        System.out.println(" |_____/|_|___/\\___|\\___/|_|  \\__,_(_)___/_/\\_\\___|");
        System.out.println(" --------------------------------------------------");
        System.out.println("  Disc0rd.exe | by scolastico | Version: PRE-ALPHA");

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
                    jda = builder.build();
                } catch (LoginException e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.print("Loading command module ");
        tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
            public void run() {
                try {
                    CommandModule eventListener = new CommandModule();

                    

                    jda.addEventListener(eventListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
