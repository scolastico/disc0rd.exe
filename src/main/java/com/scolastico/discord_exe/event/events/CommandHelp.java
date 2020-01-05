package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;


public class CommandHelp implements EventHandler, CommandHandler {

    private static ArrayList<HelpSite> helpSites = new ArrayList<HelpSite>();

    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {

        if(cmd.equalsIgnoreCase("disc0rd/help")) {
            if(args.length == 0) {
                event.getMessage().delete().queue();

                EmbedBuilder message = new EmbedBuilder();
                message.setColor(Color.green);
                message.setTitle("Disc0rd.exe | Command Help");

                for (HelpSite helpSite:helpSites) message.addField(helpSite.command, helpSite.description, false);

                event.getChannel().sendMessage(message.build()).queue();
            }

            return true;
        }

        return false;
    }

    public static void addHelpSite(HelpSite helpSite) {
        if (!helpSites.contains(helpSite)) helpSites.add(helpSite);
    }

    public static class HelpSite {
        private String command;
        private String description;

        public HelpSite(String command, String description) {
            this.command = command;
            this.description = description;
        }

        public String getCommand() {
            return command;
        }

        public String getDescription() {
            return description;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
        helpSites.add(new HelpSite("disc0rd/help", "Shows this message."));
    }
}
