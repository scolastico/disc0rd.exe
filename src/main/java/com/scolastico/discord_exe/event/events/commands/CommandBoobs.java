package com.scolastico.discord_exe.event.events.commands;

import com.scolastico.discord_exe.etc.EmoteHandler;
import com.scolastico.discord_exe.etc.Pr0grammAPI;
import com.scolastico.discord_exe.etc.Pr0grammManager;
import com.scolastico.discord_exe.etc.permissions.PermissionsManager;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.commons.io.FileUtils;

public class CommandBoobs implements EventHandler, CommandHandler {
  @Override
  public boolean respondToCommand(String cmd, String[] args, JDA jda,
                                  MessageReceivedEvent event, long senderId,
                                  long serverId, Member member) {
    if (cmd.equalsIgnoreCase("boobs")) {
      if (PermissionsManager.getInstance().checkPermission(
              event.getGuild(), member, "command-boobs")) {
        event.getMessage().delete().queue();
        Pr0grammAPI api = Pr0grammManager.getInstance().getPr0grammAPI();
        Pr0grammAPI.Pr0grammGetItemsRequestGenerator generator =
            api.generateGetItemsRequestGenerator();
        Random random = new Random();
        generator.setTags(
            "! -video & -Scheide & -Möse & -Vagina & -Oc & s:1000 & -(x:random|x:" +
            random.nextInt(1000) + ") & boobs");
        Pr0grammAPI.Pr0grammFlagCalculator calculator =
            new Pr0grammAPI.Pr0grammFlagCalculator();
        calculator.setNsfl(false);
        calculator.setNsfp(false);
        calculator.setSfw(false);
        calculator.setNsfw(true);
        generator.setFlagCalculator(calculator);
        try {
          Pr0grammAPI.Pr0grammPost[] posts = api.getPr0grammPosts(generator);
          if (posts.length != 0) {
            Pr0grammAPI.Pr0grammPost post = posts[0];
            String url = post.getImage();
            UUID uuid = UUID.randomUUID();
            File file = new File("./tmp/boobs-" + uuid.toString() + ".jpg");
            FileUtils.copyURLToFile(new URL(url), file, 1000, 2000);
            event.getChannel().sendFile(file).complete();
            file.delete();
          }
        } catch (Exception ignored) {
          Emote emoteNo = EmoteHandler.getInstance().getEmoteNo();
          event.getChannel()
              .sendMessage(
                  "<:" + emoteNo.getName() + ":" + emoteNo.getId() +
                  "> Sorry, there was an unknown exception while trying to get an image!")
              .queue();
        }
      } else {
        event.getMessage()
            .addReaction(EmoteHandler.getInstance().getEmoteNoPermission())
            .queue();
      }
      return true;
    }
    return false;
  }

  @Override
  public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
    return helpSite;
  }

  @Override
  public HashMap<String, String> getHelpSiteDetails() {
    HashMap<String, String> helpSite = new HashMap<>();
    helpSite.put("boobs", "Sends a random image of boobs.");
    return helpSite;
  }

  @Override
  public String getCommandName() {
    return "boobs";
  }

  @Override
  public void registerEvents(EventRegister eventRegister) {
    eventRegister.registerCommand(this);
    PermissionsManager.getInstance().registerPermission(
        "command-boobs",
        "Allows a user to request a random boobs picture. NSFW!", false);
  }
}
