package com.scolastico.discord_exe.event.events.commands;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.EmoteHandler;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.etc.permissions.PermissionsManager;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import java.awt.*;
import java.util.HashMap;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandBotLog implements CommandHandler, EventHandler {
  @Override
  public boolean respondToCommand(String cmd, String[] args, JDA jda,
                                  MessageReceivedEvent event, long senderId,
                                  long serverId, Member member) {
    if (cmd.equalsIgnoreCase("botLog")) {
      if (args.length == 0) {
        if (PermissionsManager.getInstance().checkPermission(
                event.getGuild(), member, "view-bot-log")) {
          for (String log : Tools.getInstance().splitSpring(
                   Disc0rd.getMysql()
                       .getServerSettings(event.getGuild().getIdLong())
                       .getLog(),
                   950)) {
            event.getChannel().sendMessage(log).queue();
          }
          return true;
        } else {
          event.getMessage()
              .addReaction(EmoteHandler.getInstance().getEmoteNoPermission())
              .queue();
          return true;
        }
      }
      Emote emoteNo = EmoteHandler.getInstance().getEmoteNo();
      event.getChannel()
          .sendMessage(
              "<:" + emoteNo.getName() + ":" + emoteNo.getId() +
              "> Sorry, but i cant find this command. Check your arguments or try `disc0rd/help botLog`.")
          .queue();
      return true;
    }
    return false;
  }

  @Override
  public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
    helpSite.put(
        "botLog",
        "Show the bot log. ATTENTION this could expose sensitive data! Admin command!");
    return helpSite;
  }

  @Override
  public HashMap<String, String> getHelpSiteDetails() {
    HashMap<String, String> ret = new HashMap<>();
    ret.put(
        "botLog",
        "Show the bot log. ATTENTION this could expose sensitive data! Admin command!");
    return ret;
  }

  @Override
  public String getCommandName() {
    return "botLog";
  }

  @Override
  public void registerEvents(EventRegister eventRegister) {
    eventRegister.registerCommand(this);
    PermissionsManager.getInstance().registerPermission(
        "view-bot-log",
        "Allows a user to use the botLog command. ATTENTION this could expose sensitive data!",
        false);
  }
}
