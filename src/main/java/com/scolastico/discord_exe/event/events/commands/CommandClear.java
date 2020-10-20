package com.scolastico.discord_exe.event.events.commands;

import com.scolastico.discord_exe.etc.EmoteHandler;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.permissions.PermissionsManager;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CommandClear implements EventHandler, CommandHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {
        if (cmd.equalsIgnoreCase("clear")) {
            Emote emoteNo = EmoteHandler.getInstance().getEmoteNo();
            if (args.length == 0) {
                event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but i cant find this command. Check your arguments or try `disc0rd/help clear`.").queue();
            } else if (args.length == 1) {
                try {
                    if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "clear")) {
                        int number = 1;
                        try {
                            number = Integer.parseInt(args[0]);
                        } catch (Exception ignored) {
                            event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but please enter only numeric values as argument!").queue();
                            return true;
                        }
                        if (number > 100 || number < 1) {
                            event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but you can only delete 1-100 messages at once!").queue();
                        } else {
                            MessageHistory messageHistory = event.getChannel().getHistoryBefore(event.getMessageIdLong(), number).complete();
                            for (Message message:messageHistory.getRetrievedHistory()) {
                                message.delete().queue();
                            }
                            event.getMessage().addReaction(EmoteHandler.getInstance().getEmoteOk()).complete();
                            event.getMessage().delete().queueAfter(10, TimeUnit.SECONDS);
                            return true;
                        }
                    } else {
                        event.getMessage().addReaction(EmoteHandler.getInstance().getEmoteNoPermission()).queue();
                    }
                } catch (Exception e) {
                    event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but an unexpected error occurred!").queue();
                    ErrorHandler.getInstance().handle(e);
                }
            } else if (args.length == 2) {
                event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but i cant find this command. Check your arguments or try `disc0rd/help clear`.").queue();
            }
            return true;
        }
        return false;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        helpSite.put("clear", "Delete a few messages at once.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> helpSite = new HashMap<>();
        helpSite.put("clear <number>", "Delete a number of messages at once (1-100)");
        return helpSite;
    }

    @Override
    public String getCommandName() {
        return "clear";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
        PermissionsManager.getInstance().registerPermission("clear", "Allow a user to use the clear command.", false);
    }
}
