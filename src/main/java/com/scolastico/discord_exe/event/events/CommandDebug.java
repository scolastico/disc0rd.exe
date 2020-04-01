package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;

public class CommandDebug implements EventHandler, CommandHandler {

    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {
        if (cmd.equalsIgnoreCase("debug")) {
            if (args.length == 0) {
                debugMain(cmd, args, jda, event, senderId, serverId);
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("emoji")) {
                    debugEmoji(cmd, args, jda, event, senderId, serverId);
                    return true;
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("emoji")) {
                    debugEmoji(cmd, args, jda, event, senderId, serverId);
                    return true;
                }
            }
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.red);
            embedBuilder.setTitle("Sorry,");
            embedBuilder.setDescription("but i cant see this debug option!");
            event.getChannel().sendMessage(embedBuilder.build()).queue();
            return true;
        }
        return false;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        helpSite.put("debug", "Debug utilities for the bot.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> helpSite = new HashMap<>();
        helpSite.put("debug", "Outputs guild information's for debug.");
        helpSite.put("debug emoji", "Outputs the emoji id's from this guild.");
        return helpSite;
    }

    @Override
    public String getCommandName() {
        return "debug";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
    }

    private void debugMain(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.YELLOW);
        builder.setTitle("Debug: General Information");
        builder.setDescription("" +
                        "**Version: **`" + Disc0rd.getVersion() + "`\n" +
                        "**Guild Id: **`" + event.getGuild().getId() + "`"
        );
        event.getChannel().sendMessage(builder.build()).queue();
    }

    private void debugEmoji(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.yellow);
        int site = 1;
        if (args.length == 2) {
            try {
                site = Integer.parseInt(args[1]);
            } catch (Exception ignored) {
                site = 0;
            }
        }
        HashMap<String, String> pagesRaw = new HashMap<>();
        for (Emote emote:event.getGuild().getEmotes()) {
            pagesRaw.put("<:" + emote.getName() + ":" + emote.getId() + ">", "Emote Name: `" + emote.getName() + "`\nEmote ID: `" + emote.getId() + "`");
        }
        HashMap<Integer, HashMap<String, String>> pages = Tools.getInstance().splitToSites(pagesRaw);
        embedBuilder.setTitle("Debug: Emoji | Page `" + site + "` from `" + pages.size() + "`");
        if (pages.containsKey(site)) {
            for (String key:pages.get(site).keySet()) {
                embedBuilder.addField(key, pages.get(site).get(key), true);
            }
            embedBuilder.setFooter("To see other pages enter 'disc0rd/debug emoji <site>'!");
        } else {
            embedBuilder.setTitle("Sorry,");
            embedBuilder.setDescription("i cant find this page!");
            embedBuilder.setColor(Color.red);
        }
        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }

}
