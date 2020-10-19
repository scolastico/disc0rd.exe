package com.scolastico.discord_exe;

import com.scolastico.discord_exe.config.ConfigDataStore;
import com.scolastico.discord_exe.config.ConfigHandler;
import com.scolastico.discord_exe.etc.*;
import com.scolastico.discord_exe.etc.musicplayer.SpotifyToYoutube;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventManager;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.mysql.MysqlHandler;
import com.scolastico.discord_exe.webserver.WebServerManager;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.reflections.Reflections;

public class Disc0rd {

  private static ConfigHandler configHandler;
  private static ConfigDataStore config;
  private static MysqlHandler mysql;
  private static JDA jda;
  private static boolean ready = false;
  private static String version = "Can't read Version! This build is corrupt!";
  private static EventRegister eventRegister;
  private static Long executedCommands = 0L;
  private static final long startTime = System.currentTimeMillis();
  private static final CloseableHttpClient httpClient =
      HttpClients.createDefault();
  private static final ArrayList<Runnable> onExitRuns = new ArrayList<>();

  private static final Thread onExit = new Thread() {
    public void run() {
      for (Runnable runnable : onExitRuns) {
        try {
          runnable.run();
        } catch (Exception e) {
          ErrorHandler.getInstance().handle(e);
        }
      }
    }
  };

  public static void addOnExitRunnable(Runnable runnable) {
    onExitRuns.add(runnable);
  }

  public static CloseableHttpClient getHttpClient() { return httpClient; }

  public static long getStartTime() { return startTime; }

  public static void setConfig(ConfigDataStore config) {
    Disc0rd.config = config;
  }

  public static ConfigHandler getConfigHandler() { return configHandler; }

  public static void addExecutedCommand() { executedCommands++; }

  public static Long getExecutedCommands() { return executedCommands; }

  public static JDA getJda() { return jda; }

  public static EventRegister getEventRegister() { return eventRegister; }

  public static String getVersion() { return version; }

  public static boolean isReady() { return ready; }

  public static MysqlHandler getMysql() { return mysql; }

  public static ConfigDataStore getConfig() { return config; }

  public static void main(String[] args) {

    try {
      version = "c-" + VersionController.getCommit();
    } catch (Exception ignored) {
    }

    System.out.println("  _____  _           ___          _                ");
    System.out.println(" |  __ \\(_)         / _ \\        | |               ");
    System.out.println(" | |  | |_ ___  ___| | | |_ __ __| |  _____  _____ ");
    System.out.println(
        " | |  | | / __|/ __| | | | '__/ _\\ | / _ \\ \\/ / _ \\");
    System.out.println(" | |__| | \\__ \\ (__| |_| | | | (_| ||  __/>  <  __/");
    System.out.println(
        " |_____/|_|___/\\___|\\___/|_|  \\__,_(_)___/_/\\_\\___|");
    System.out.println(" --------------------------------------------------");
    System.out.println("Disc0rd.exe | by scolastico | Version: " + version);
    System.out.println();

    Runtime.getRuntime().addShutdownHook(onExit);

    Tools tools = Tools.getInstance();

    System.out.print("Loading configuration module ");
    tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
      public void run() {
        try {
          configHandler =
              new ConfigHandler(new ConfigDataStore(), "config.json", true);
          Object obj = configHandler.getConfigObject();
          if (obj instanceof ConfigDataStore) {
            config = (ConfigDataStore)obj;
          } else {
            ErrorHandler.getInstance().handleFatal(new Exception(
                "Config not valid! Please delete your config and try again!"));
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
          mysql = new MysqlHandler(
              config.getMysql().getServer(), config.getMysql().getUser(),
              config.getMysql().getPass(), config.getMysql().getDatabase(),
              config.getMysql().getPrefix());
        } catch (Exception e) {
          ErrorHandler.getInstance().handleFatal(e);
        }
      }
    });

    System.out.print("Loading java discord api module ");
    tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
      public void run() {
        try {
          JDABuilder builder = new JDABuilder(config.getDiscordToken());
          builder.setAutoReconnect(true);
          jda = builder.build().awaitReady();
        } catch (LoginException | InterruptedException e) {
          ErrorHandler.getInstance().handleFatal(e);
        }
      }
    });

    System.out.print("Loading event module ");
    tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
      public void run() {
        try {
          eventRegister = EventRegister.getInstance();

          Reflections reflections =
              new Reflections("com.scolastico.discord_exe");
          Set<Class<? extends EventHandler>> eventHandlers =
              reflections.getSubTypesOf(EventHandler.class);
          for (Class<?> eventHandler : eventHandlers) {
            Object obj = eventHandler.newInstance();
            if (obj instanceof EventHandler) {
              ((EventHandler)obj).registerEvents(eventRegister);
            }
          }

          jda.addEventListener(eventRegister);
        } catch (Exception e) {
          ErrorHandler.getInstance().handleFatal(e);
        }
      }
    });

    System.out.print("Loading schedule module ");
    tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
      public void run() {
        try {
          eventRegister.registerSchedule(ScheduleTask.getInstance());
          Timer timer = new Timer();
          timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
              eventRegister.fireSchedule();
            }
          }, 0, 50);
        } catch (Exception e) {
          ErrorHandler.getInstance().handleFatal(e);
        }
      }
    });

    System.out.print("Loading web server module ");
    tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
      public void run() {
        try {
          WebServerManager.getInstance();
        } catch (Exception e) {
          ErrorHandler.getInstance().handleFatal(e);
        }
      }
    });

    System.out.print("Loading extended event system module ");
    tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
      public void run() {
        try {
          ExtendedEventManager.getInstance();
        } catch (Exception e) {
          ErrorHandler.getInstance().handleFatal(e);
        }
      }
    });

    System.out.print("Loading pr0gramm module ");
    tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
      public void run() {
        try {
          Pr0grammManager.getInstance();
        } catch (Exception e) {
          ErrorHandler.getInstance().handleFatal(e);
        }
      }
    });

    System.out.print("Loading spotify to youtube module ");
    tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
      public void run() {
        try {
          SpotifyToYoutube.getInstance();
        } catch (Exception e) {
          ErrorHandler.getInstance().handleFatal(e);
        }
      }
    });

    System.out.print("Loading extra functions ");
    tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
      public void run() {
        try {
          File tmpFolder = new File(config.getTmpDir());
          if (!tmpFolder.exists())
            tmpFolder.mkdirs();
          if (tmpFolder.isDirectory()) {
            for (String entry : tmpFolder.list()) {
              File file = new File(tmpFolder.getPath(), entry);
              if (!file.delete()) {
                throw new Exception("cant delete file '" + file.getPath() +
                                    "' in tmp dir");
              }
            }
          } else {
            throw new Exception("tmp dir is not a dir");
          }
        } catch (Exception e) {
          ErrorHandler.getInstance().handleFatal(e);
        }
      }
    });

    System.out.print("Starting garbage collector scheduler ");
    tools.asyncLoadingAnimationWhileWaitingResult(new Runnable() {
      public void run() {
        try {
          ScheduleTask.getInstance().runScheduledTaskRepeat(new Runnable() {
            @Override
            public void run() {
              try {
                System.gc();
              } catch (Exception e) {
                ErrorHandler.getInstance().handle(e);
              }
            }
          }, config.getGarbageCollectorTime(), 1, true);
        } catch (Exception e) {
          ErrorHandler.getInstance().handleFatal(e);
        }
      }
    });

    addOnExitRunnable(new Runnable() {
      @Override
      public void run() {
        try {
          httpClient.close();
        } catch (IOException e) {
          ErrorHandler.getInstance().handle(e);
        }
      }
    });

    tools.generateNewSpacesInConsole(1);
    System.out.println("Loading Finished! Took " +
                       (System.currentTimeMillis() - startTime) + " ms!");
    tools.generateNewSpacesInConsole(1);

    ready = true;
  }
}
