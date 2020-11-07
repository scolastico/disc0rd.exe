package com.scolastico.discord_exe.event.events.commands;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;

public class CommandDisc0rd implements CommandHandler, EventHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {
        if (cmd.equalsIgnoreCase("disc0rd")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.setTitle("Sorry,");
            builder.setDescription("but the command isn't correct. Please check the arguments or try `disc0rd/help disc0rd`.");
            if (args.length == 0) {
                builder.setColor(Color.YELLOW);
                builder.setTitle("Disc0rd.exe - About");
                builder.setDescription(
                                "Currently Version: " + Disc0rd.getVersion() + "\n" +
                                "Developer: " + Disc0rd.getOwner().getAsTag() + "\n" +
                                "Website: <" + Disc0rd.getConfig().getWebServer().getDomain() + ">\n" +
                                "Documentation: <" + Disc0rd.getConfig().getDocumentation() + ">\n" +
                                "\n" +
                                "You can get support via...\n" +
                                " ... email: " + Disc0rd.getConfig().getEmail() + "\n" +
                                " ... discord: " + Disc0rd.getConfig().getDiscordInvite() + "\n" +
                                "\n" +
                                "Special thanks to: \n" +
                                "RundesBalli for helping me with the RegEx <https://RundesBalli.com/>"
                );
                builder.setFooter("Copyright 2020 Disc0rd.exe - https://scolasti.co/");
            }
            event.getChannel().sendMessage(builder.build()).queue();
            return true;
        }
        return false;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        helpSite.put("disc0rd", "Shows some information about Disc0rd.exe.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("disc0rd", "Shows some information about Disc0rd.exe.");
        return ret;
    }

    @Override
    public String getCommandName() {
        return "disc0rd";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
    }
}
