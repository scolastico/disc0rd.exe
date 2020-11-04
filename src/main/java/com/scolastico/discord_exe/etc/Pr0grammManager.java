package com.scolastico.discord_exe.etc;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.config.ConfigHandler;
import com.scolastico.discord_exe.config.Pr0grammConfigDataStore;
import com.scolastico.discord_exe.mysql.MysqlHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Pr0grammManager {

  private static Pr0grammManager instance = null;
  private Pr0grammAPI pr0grammAPI;
  private ConfigHandler configHandler;
  private Pr0grammConfigDataStore config;
  private HashMap<Long, ServerSettings.Pr0grammServerConfig>
      pr0grammServerConfigs;

  private Pr0grammManager() {
    try {
      configHandler = new ConfigHandler(new Pr0grammConfigDataStore(),
                                        "pr0gramm.json", true);
      Object obj = configHandler.getConfigObject();
      if (obj instanceof Pr0grammConfigDataStore) {
        config = (Pr0grammConfigDataStore)obj;
      } else {
        throw new Exception(
            "Config not valid! Please delete your config and try again!");
      }
      if (config.getToken() == null || config.getUsername() == null ||
          config.getPassword() == null)
        throw new Exception(
            "Config not valid! Please delete your config and try again!");
      try {
        pr0grammAPI = new Pr0grammAPI(config.getToken());
      } catch (Pr0grammAPI.Pr0grammLoginError error) {
        pr0grammAPI =
            new Pr0grammAPI(config.getUsername(), config.getPassword());
        config.setToken(pr0grammAPI.getToken());
        config.setPassword("password");
        configHandler.setConfigObject(config);
        configHandler.saveConfigObject();
      }
      reloadConfigDataSore();
      ScheduleTask.getInstance().runScheduledTaskRepeat(new Runnable() {
        @Override
        public void run() {
          try {
            checkAllConnectedServersAndClear();
          } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
          }
        }
      }, config.getCheckUsersAndClearSchedule() * 20, 1, true);
      ScheduleTask.getInstance().runScheduledTaskRepeat(new Runnable() {
        @Override
        public void run() {
          if (Disc0rd.isReady()) {
            try {
              checkForNewPosts();
            } catch (Exception e) {
              ErrorHandler.getInstance().handle(e);
            }
          }
        }
      }, config.getCheckSchedule() * 20, 2, true);
      Disc0rd.addOnExitRunnable(new Runnable() {
        @Override
        public void run() {
          try {
            pr0grammAPI.close();
          } catch (IOException ignored) {
          }
        }
      });
    } catch (Exception e) {
      ErrorHandler.getInstance().handleFatal(e);
    }
  }

  public static Pr0grammManager getInstance() {
    if (instance == null) {
      instance = new Pr0grammManager();
    }
    return instance;
  }

  public void checkForNewPosts() {
    try {
      Pr0grammAPI.Pr0grammGetItemsRequestGenerator generator =
          pr0grammAPI.generateGetItemsRequestGenerator();
      Pr0grammAPI.Pr0grammFlagCalculator calculator =
          new Pr0grammAPI.Pr0grammFlagCalculator();
      calculator.setSfw(true);
      calculator.setNsfp(true);
      calculator.setNsfw(false);
      calculator.setNsfl(false);
      generator.setFlagCalculator(calculator);
      generator.setPromoted(false);
      if (config.getLastPost() != 0) {
        generator.setNewer(config.getLastPost());
      }
      Pr0grammAPI.Pr0grammPost[] postsSFW =
          pr0grammAPI.getPr0grammPosts(generator);
      calculator.setSfw(false);
      calculator.setNsfp(false);
      calculator.setNsfw(true);
      generator.setFlagCalculator(calculator);
      Pr0grammAPI.Pr0grammPost[] postsNSFW =
          pr0grammAPI.getPr0grammPosts(generator);
      calculator.setNsfw(false);
      calculator.setNsfl(true);
      generator.setFlagCalculator(calculator);
      Pr0grammAPI.Pr0grammPost[] postsNSFL =
          pr0grammAPI.getPr0grammPosts(generator);
      HashMap<Integer, Pr0grammAPI.Pr0grammPost[]> posts = new HashMap<>();
      posts.put(1, postsSFW);
      posts.put(2, postsNSFW);
      posts.put(3, postsNSFL);
      for (Integer flag : posts.keySet()) {
        for (Pr0grammAPI.Pr0grammPost post : posts.get(flag)) {
          if (post.getId() > config.getLastPost()) {
            config.setLastPost(post.getId());
          }
          for (Long guildId : pr0grammServerConfigs.keySet()) {
            Guild guild = Disc0rd.getJda().getGuildById(guildId);
            if (guild == null)
              continue;
            ServerSettings.Pr0grammServerConfig config =
                pr0grammServerConfigs.get(guildId);
            for (ServerSettings.Pr0grammServerConfig.Pr0grammSubscription
                     subscription : config.getSubscriptions()) {
              if (subscription.getUsername().equals(post.getUser())) {
                if ((flag == 1 && subscription.isSfw()) ||
                    (flag == 2 && subscription.isNsfw()) ||
                    (flag == 3 && subscription.isNsfl())) {
                  TextChannel channel =
                      guild.getTextChannelById(subscription.getChannel());
                  if (channel == null)
                    continue;
                  EmbedBuilder builder = new EmbedBuilder();
                  builder.setTitle("Pr0gramm.com");
                  builder.setDescription(
                      "New upload found! <https://pr0gramm.com/new/" +
                      post.getId() + ">");
                  builder.setColor(new Color(0xEE4D2E));
                  builder.setAuthor(post.getUser(),
                                    "https://pr0gramm.com/user/" +
                                        post.getUser());
                  builder.setImage(post.getImage());
                  if (post.getFull() != null)
                    builder.setFooter(
                        "To see in full resolution: " + post.getFull() + "");
                  channel.sendMessage(builder.build()).queue();
                }
              }
            }
          }
        }
      }
      configHandler.setConfigObject(config);
      configHandler.saveConfigObject();
    } catch (Pr0grammAPI.Pr0grammApiError | IOException e) {
      ErrorHandler.getInstance().handle(e);
    }
  }

  public void reloadConfigDataSore() {
    pr0grammServerConfigs = new HashMap<>();
    MysqlHandler handler = Disc0rd.getMysql();
    HashMap<Long, ServerSettings> serverSettingsHashMap =
        handler.getAllServerSettingsWithId();
    for (Long id : serverSettingsHashMap.keySet()) {
      if (serverSettingsHashMap.get(id)
              .getPr0grammServerConfig()
              .getLinkedAccount() != null) {
        pr0grammServerConfigs.put(
            id, serverSettingsHashMap.get(id).getPr0grammServerConfig());
      }
    }
  }

  public boolean authGuild(long id, String username, long memberId) {
    if (isAuthorizedGuild(id))
      return false;
    MysqlHandler handler = Disc0rd.getMysql();
    ServerSettings settings = handler.getServerSettings(id);
    ServerSettings.Pr0grammServerConfig config =
        settings.getPr0grammServerConfig();
    config.setLinkedAccount(username);
    config.setLinkedMember(memberId);
    pr0grammServerConfigs.put(id, config);
    settings.setPr0grammServerConfig(config);
    handler.setServerSettings(id, settings);
    return true;
  }

  public boolean deAuthGuild(long id) {
    if (!isAuthorizedGuild(id))
      return false;
    pr0grammServerConfigs.remove(id);
    MysqlHandler handler = Disc0rd.getMysql();
    ServerSettings settings = handler.getServerSettings(id);
    ServerSettings.Pr0grammServerConfig serverConfig =
        settings.getPr0grammServerConfig();
    serverConfig.setLinkedAccount(null);
    serverConfig.setLinkedMember(0L);
    settings.setPr0grammServerConfig(serverConfig);
    handler.setServerSettings(id, settings);
    return true;
  }

  public boolean isAuthorizedGuild(long id) {
    return pr0grammServerConfigs.containsKey(id);
  }

  public ArrayList<ServerSettings.Pr0grammServerConfig.Pr0grammSubscription>
  getSubsFromGuild(long id) {
    if (!isAuthorizedGuild(id))
      return null;
    return pr0grammServerConfigs.get(id).getSubscriptions();
  }

  public boolean subscribeUser(long guildId, long channelId, String username) {
    if (!isAuthorizedGuild(guildId))
      return false;
    MysqlHandler handler = Disc0rd.getMysql();
    ServerSettings.Pr0grammServerConfig serverConfig =
        pr0grammServerConfigs.get(guildId);
    for (ServerSettings.Pr0grammServerConfig.Pr0grammSubscription subscription :
         serverConfig.getSubscriptions()) {
      if (subscription.getChannel() == channelId &&
          subscription.getUsername().equals(username))
        return false;
    }
    serverConfig.addSubscription(
        new ServerSettings.Pr0grammServerConfig.Pr0grammSubscription(
            username, true, false, false, channelId));
    ServerSettings settings = handler.getServerSettings(guildId);
    settings.setPr0grammServerConfig(serverConfig);
    handler.setServerSettings(guildId, settings);
    return true;
  }

  public boolean unSubscribeUser(long guildId, long channelId,
                                 String username) {
    if (!isAuthorizedGuild(guildId))
      return false;
    MysqlHandler handler = Disc0rd.getMysql();
    ServerSettings.Pr0grammServerConfig serverConfig =
        pr0grammServerConfigs.get(guildId);
    ServerSettings.Pr0grammServerConfig.Pr0grammSubscription toDelete = null;
    for (ServerSettings.Pr0grammServerConfig.Pr0grammSubscription subscription :
         serverConfig.getSubscriptions()) {
      if (subscription.getChannel() == channelId &&
          subscription.getUsername().equals(username)) {
        toDelete = subscription;
      }
    }
    if (toDelete != null) {
      serverConfig.removeSubscription(toDelete);
      ServerSettings settings = handler.getServerSettings(guildId);
      settings.setPr0grammServerConfig(serverConfig);
      handler.setServerSettings(guildId, settings);
      return true;
    }
    return false;
  }

  public boolean setFollowedChannels(long guildId, long channelId,
                                     String username, boolean sfw, boolean nsfw,
                                     boolean nsfl) {
    if (!isAuthorizedGuild(guildId))
      return false;
    MysqlHandler handler = Disc0rd.getMysql();
    ServerSettings.Pr0grammServerConfig serverConfig =
        pr0grammServerConfigs.get(guildId);
    ServerSettings.Pr0grammServerConfig.Pr0grammSubscription toDelete = null;
    ServerSettings.Pr0grammServerConfig.Pr0grammSubscription toAdd = null;
    for (ServerSettings.Pr0grammServerConfig.Pr0grammSubscription subscription :
         serverConfig.getSubscriptions()) {
      if (subscription.getChannel() == channelId &&
          subscription.getUsername().equals(username)) {
        toDelete = subscription;
        toAdd = new ServerSettings.Pr0grammServerConfig.Pr0grammSubscription(
            username, sfw, nsfw, nsfl, channelId);
      }
    }
    if (toDelete != null) {
      serverConfig.removeSubscription(toDelete);
      serverConfig.addSubscription(toAdd);
      ServerSettings settings = handler.getServerSettings(guildId);
      settings.setPr0grammServerConfig(serverConfig);
      handler.setServerSettings(guildId, settings);
      return true;
    }
    return false;
  }

  public void checkAllConnectedServersAndClear() {
    JDA jda = Disc0rd.getJda();
    MysqlHandler handler = Disc0rd.getMysql();
    ArrayList<Long> toDelete = new ArrayList<>();
    EmbedBuilder builder = new EmbedBuilder();
    builder.setTitle("Sorry,");
    builder.setDescription(
        "but the linked Pr0gramm account is not longer valid! There could be several reasons! E.g. the connected account has been banned or the account owner is no longer guild owner!");
    builder.setColor(Color.red);
    for (long key : pr0grammServerConfigs.keySet()) {
      ServerSettings.Pr0grammServerConfig config =
          pr0grammServerConfigs.get(key);
      Guild guild = jda.getGuildById(key);
      if (guild != null) {
        Member member = guild.retrieveMemberById(config.getLinkedMember()).complete();
        if (member != null) {
          if (Tools.getInstance().isOwner(guild, member.getUser())) {
            try {
              Pr0grammAPI.Pr0grammUser user =
                  pr0grammAPI.getPr0grammUser(config.getLinkedAccount());
              if (user.getBanned() == 0) {
                continue;
              }
            } catch (Pr0grammAPI.Pr0grammApiError ignored) {
              continue;
            }
          }
        }
        TextChannel channel = guild.getDefaultChannel();
        if (channel != null) {
          channel.sendMessage(builder.build()).queue();
        }
      }
      toDelete.add(key);
    }
    for (Long key : toDelete) {
      ServerSettings settings = handler.getServerSettings(key);
      ServerSettings.Pr0grammServerConfig serverConfig =
          settings.getPr0grammServerConfig();
      serverConfig.setLinkedMember(0L);
      serverConfig.setLinkedAccount(null);
      settings.setPr0grammServerConfig(serverConfig);
      handler.setServerSettings(key, settings);
      pr0grammServerConfigs.remove(key);
    }
  }

  public Pr0grammAPI getPr0grammAPI() { return pr0grammAPI; }

  public ConfigHandler getConfigHandler() { return configHandler; }

  public Pr0grammConfigDataStore getConfig() { return config; }
}
