package com.scolastico.discord_exe.event.events;

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
            event.getMessage().delete().queue();
            if (args.length == 0) {
                debugMain(cmd, args, jda, event, senderId, serverId);
                return true;
            } else if (args.length == 1) {
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

    }

    private void debugEmoji(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.yellow);
        embedBuilder.setTitle("Debug: Emoji");
        for (Emote emote:event.getGuild().getEmotes()) {
            embedBuilder.addField("<:" + emote.getName() + ":" + emote.getId() + "> ***" + emote.getName() + "***", emote.getId(), true);
        }
        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }

}
