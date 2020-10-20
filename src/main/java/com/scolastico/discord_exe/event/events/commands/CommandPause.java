package com.scolastico.discord_exe.event.events.commands;

import com.scolastico.discord_exe.etc.EmoteHandler;
import com.scolastico.discord_exe.etc.musicplayer.MusicPlayer;
import com.scolastico.discord_exe.etc.musicplayer.MusicPlayerRegister;
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
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandPause implements CommandHandler, EventHandler {
  @Override
  public boolean respondToCommand(String cmd, String[] args, JDA jda,
                                  MessageReceivedEvent event, long senderId,
                                  long serverId, Member member) {
    if (cmd.equalsIgnoreCase("pause")) {
      Emote emoteNo = EmoteHandler.getInstance().getEmoteNo();
      if (args.length == 0) {
        if (PermissionsManager.getInstance().checkPermission(event.getGuild(),
                                                             member, "pause")) {
          if (member.getVoiceState() != null) {
            VoiceChannel channel = member.getVoiceState().getChannel();
            if (channel != null) {
              MusicPlayer player = MusicPlayerRegister.getInstance().getPlayer(
                  event.getGuild().getIdLong());
              if (player != null) {
                if (player.getChannel() == channel) {
                  if (player.getStatus()) {
                    player.pause();
                    event.getMessage()
                        .addReaction(EmoteHandler.getInstance().getEmotePause())
                        .queue();
                  } else {
                    player.play();
                    event.getMessage()
                        .addReaction(EmoteHandler.getInstance().getEmotePlay())
                        .queue();
                  }
                } else {
                  event.getChannel()
                      .sendMessage(
                          "<:" + emoteNo.getName() + ":" + emoteNo.getId() +
                          "> Sorry, but you need to be in the same channel as the bot.")
                      .queue();
                }
              } else {
                event.getChannel()
                    .sendMessage(
                        "<:" + emoteNo.getName() + ":" + emoteNo.getId() +
                        "> Sorry, but there is no player currently. You can start the music player with `disc0rd/play <url>`.")
                    .queue();
              }
            } else {
              event.getChannel()
                  .sendMessage(
                      "<:" + emoteNo.getName() + ":" + emoteNo.getId() +
                      "> Sorry, but you need to be in a voice channel.")
                  .queue();
            }
          }
        } else {
          event.getMessage()
              .addReaction(EmoteHandler.getInstance().getEmoteNoPermission())
              .queue();
        }
      } else {
        event.getChannel()
            .sendMessage(
                "<:" + emoteNo.getName() + ":" + emoteNo.getId() +
                "> Sorry, but i cant find this command. Check your arguments or try `disc0rd/help pause`.")
            .queue();
      }
      return true;
    }
    return false;
  }

  @Override
  public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
    helpSite.put("pause", "Pause the music player.");
    return helpSite;
  }

  @Override
  public HashMap<String, String> getHelpSiteDetails() {
    HashMap<String, String> ret = new HashMap<>();
    ret.put("pause", "Pause the music player.");
    return ret;
  }

  @Override
  public String getCommandName() {
    return "pause";
  }

  @Override
  public void registerEvents(EventRegister eventRegister) {
    eventRegister.registerCommand(this);
    PermissionsManager.getInstance().registerPermission(
        "pause", "Allow a user to use the pause command from the music player.",
        true);
  }
}
