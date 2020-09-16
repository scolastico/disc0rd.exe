package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.permissions.PermissionsManager;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CommandClear implements EventHandler, CommandHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {
        if (cmd.equalsIgnoreCase("clear")) {
            EmbedBuilder builder = new EmbedBuilder();
            if (args.length == 0) {
                builder.setTitle("Sorry,");
                builder.setDescription("but you use this command wrong! Try using `disc0rd/clear <number>`.");
                builder.setColor(Color.yellow);
            } else if (args.length == 1) {
                try {
                    Member member = event.getGuild().getMember(event.getAuthor());
                    if (member != null) {
                        if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "clear")) {
                            int number = 1;
                            try {
                                number = Integer.parseInt(args[0]);
                            } catch (Exception ignored) {
                                builder.setTitle("Sorry,");
                                builder.setDescription("but please enter only numeric values as argument!");
                                builder.setColor(Color.red);
                                event.getChannel().sendMessage(builder.build()).queue();
                                return true;
                            }
                            if (number > 200 || number < 1) {
                                builder.setTitle("Sorry,");
                                builder.setDescription("but you can only delete 1-200 messages at once!");
                                builder.setColor(Color.red);
                            } else {
                                MessageHistory messageHistory = event.getChannel().getHistoryBefore(event.getMessageIdLong(), number).complete();
                                for (Message message:messageHistory.getRetrievedHistory()) {
                                    message.delete().queue();
                                }
                                event.getMessage().delete().queue();
                                builder.setTitle("Success,");
                                builder.setDescription("i clearing the chat for you!");
                                builder.setFooter("This message deletes itself after 20 seconds!");
                                builder.setColor(Color.green);
                                event.getChannel().sendMessage(builder.build()).complete().delete().queueAfter(20, TimeUnit.SECONDS);
                                return true;
                            }
                        } else {
                            builder.setTitle("Sorry,");
                            builder.setDescription("but you can only delete 1-200 messages at once!");
                            builder.setColor(Color.red);
                        }
                    } else {
                        builder.setTitle("Sorry,");
                        builder.setDescription("but you dont have the permission to use this command!");
                        builder.setColor(Color.red);
                    }
                } catch (Exception e) {
                    builder.setTitle("Sorry,");
                    builder.setDescription("but an unexpected error occurred!");
                    builder.setColor(Color.red);
                    ErrorHandler.getInstance().handle(e);
                }
            } else if (args.length == 2) {
                builder.setTitle("Sorry,");
                builder.setDescription("but you use this command wrong! Try using `disc0rd/clear <number>`.");
                builder.setColor(Color.red);
            }
            event.getChannel().sendMessage(builder.build()).queue();
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
        helpSite.put("clear <number>", "Delete a number of messages at once (1-200)");
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
