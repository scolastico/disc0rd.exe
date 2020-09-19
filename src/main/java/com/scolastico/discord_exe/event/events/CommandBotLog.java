package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.etc.permissions.PermissionsManager;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;

public class CommandBotLog implements CommandHandler, EventHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {
        if (cmd.equalsIgnoreCase("botLog")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.setTitle("Sorry,");
            builder.setDescription("but i cant find this command. Check your arguments or try `disc0rd/help botLog`.");
            if (args.length == 0) {
                if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "view-bot-log")) {
                    for (String log:Tools.getInstance().splitSpring(Disc0rd.getMysql().getServerSettings(event.getGuild().getIdLong()).getLog(), 950)) {
                        event.getChannel().sendMessage(log).queue();
                    }
                    return true;
                } else {
                    builder.setDescription("but you dont have the permission to use this command!");
                }
            }
            event.getChannel().sendMessage(builder.build()).queue();
            return true;
        }
        return false;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        helpSite.put("botLog", "Show the bot log. ATTENTION this could expose sensitive data! Admin command!");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("botLog", "Show the bot log. ATTENTION this could expose sensitive data! Admin command!");
        return ret;
    }

    @Override
    public String getCommandName() {
        return "botLog";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
        PermissionsManager.getInstance().registerPermission("view-bot-log", "Allows a user to use the botLog command. ATTENTION this could expose sensitive data!", false);
    }
}
