package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;


public class CommandHelp implements EventHandler, CommandHandler {

    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {

        if(cmd.equalsIgnoreCase("help")) {
            EmbedBuilder message = new EmbedBuilder();
            if(args.length == 0) {
                event.getMessage().delete().queue();

                message.setColor(Color.green);
                message.setTitle("Disc0rd.exe | Command Help");

                ArrayList<CommandHandler> commandHandlers = Disc0rd.getEventRegister().getCommandHandlers();

                HashMap<String, String> helpSite = new HashMap<>();

                ServerSettings settings = Disc0rd.getMysql().getServerSettings(event.getGuild().getIdLong());

                for (CommandHandler handler:commandHandlers) {
                    helpSite = handler.getHelpSite(helpSite);
                }

                for (String key:helpSite.keySet()) {
                    message.addField("`" + settings.getCmdPrefix() + key + "`", helpSite.get(key), true);
                }

                event.getChannel().sendMessage(message.build()).queue();
            } else if (args.length == 1) {
                event.getMessage().delete().queue();

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
