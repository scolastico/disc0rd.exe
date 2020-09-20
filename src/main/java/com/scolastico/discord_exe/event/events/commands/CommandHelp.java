package com.scolastico.discord_exe.event.events.commands;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


public class CommandHelp implements EventHandler, CommandHandler {

    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {

        if(cmd.equalsIgnoreCase("help")) {
            EmbedBuilder message = new EmbedBuilder();
            if(args.length == 0) {

                message.setColor(Color.green);

                ArrayList<CommandHandler> commandHandlers = Disc0rd.getEventRegister().getCommandHandlers();

                HashMap<String, String> helpSite = new HashMap<>();

                ServerSettings settings = Disc0rd.getMysql().getServerSettings(event.getGuild().getIdLong());

                for (CommandHandler handler:commandHandlers) {
                    helpSite = handler.getHelpSite(helpSite);
                }

                HashMap<Integer, HashMap<String, String>> pages = Tools.getInstance().splitToSites(helpSite);

                message.setTitle("Disc0rd.exe | Command Help Page `1` from `" + pages.size() + "`");

                for (String key:pages.get(1).keySet()) {
                    message.addField(key, pages.get(1).get(key), true);
                }

                message.setFooter("To see other pages enter 'disc0rd/help <page>'!");

                event.getChannel().sendMessage(message.build()).queue();

            } else if (args.length == 1) {

                try {
                    Integer page = Integer.parseInt(args[0]);

                    message.setColor(Color.green);

                    ArrayList<CommandHandler> commandHandlers = Disc0rd.getEventRegister().getCommandHandlers();

                    HashMap<String, String> helpSite = new HashMap<>();

                    ServerSettings settings = Disc0rd.getMysql().getServerSettings(event.getGuild().getIdLong());

                    for (CommandHandler handler:commandHandlers) {
                        helpSite = handler.getHelpSite(helpSite);
                    }

                    HashMap<Integer, HashMap<String, String>> pages = Tools.getInstance().splitToSites(helpSite);

                    message.setTitle("Disc0rd.exe | Command Help Page `1` from `" + pages.size() + "`");

                    if (!pages.containsKey(page)) {
                        message.setColor(Color.red);
                        message.setTitle("Sorry,");
                        message.setDescription("but i cant find this page!");
                        event.getChannel().sendMessage(message.build()).queue();
                        return true;
                    }

                    for (String key:pages.get(page).keySet()) {
                        message.addField(key, pages.get(page).get(key), true);
                    }

                    message.setFooter("To see other pages enter 'disc0rd/help <page>'!");

                    event.getChannel().sendMessage(message.build()).queue();

                    return true;
                } catch (Exception ignored) {

                    message.setColor(Color.green);

                    ArrayList<CommandHandler> commandHandlers = Disc0rd.getEventRegister().getCommandHandlers();

                    HashMap<String, String> helpSite = null;

                    ServerSettings settings = Disc0rd.getMysql().getServerSettings(event.getGuild().getIdLong());

                    for (CommandHandler handler:commandHandlers) {
                        if (handler.getCommandName() != null) {
                            if (handler.getCommandName().equalsIgnoreCase(args[0])) {
                                helpSite = handler.getHelpSiteDetails();
                                message.setTitle("Disc0rd.exe | Command Help for `" + handler.getCommandName() + "`");
                            }
                        }
                    }

                    if (helpSite == null) {
                        message.setColor(Color.red);
                        message.setTitle("Sorry,");
                        message.setDescription("but i cant find this help site.");
                        event.getChannel().sendMessage(message.build()).queue();
                        return true;
                    }

                    for (String key:helpSite.keySet()) {
                        message.addField("`" + settings.getCmdPrefix() + key + "`", helpSite.get(key), true);
                    }

                    event.getChannel().sendMessage(message.build()).queue();
                }
            } else {
                message.setColor(Color.red);
                message.setTitle("Sorry,");
                message.setDescription("but this sub command doesnt exist.");
                event.getChannel().sendMessage(message.build()).queue();
            }
            return true;
        }

        return false;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        helpSite.put("help", "Shows this help site.");
        helpSite.put("help <command>", "Shows the sub commands of an command.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> helpSite = new HashMap<>();
        helpSite.put("help", "Shows this help site.");
        helpSite.put("help <command>", "Shows the sub commands of an command.");
        return helpSite;
    }

    @Override
    public String getCommandName() {
        return "help";
    }

    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
    }
}
