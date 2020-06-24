package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;

public class CommandPrefixChange implements EventHandler, CommandHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {
        if (cmd.equalsIgnoreCase("prefixChange")) {
            EmbedBuilder builder = new EmbedBuilder();
            if (args.length == 1) {
                if (Tools.getInstance().isOwner(event.getGuild(), event.getAuthor())) {
                    ServerSettings settings = Disc0rd.getMysql().getServerSettings(event.getGuild().getIdLong());
                    settings.setCmdPrefix(args[0]);
                    Disc0rd.getMysql().setServerSettings(event.getGuild().getIdLong(), settings);
                    builder.setColor(Color.green);
                    builder.setTitle("Success,");
                    builder.setDescription("the command prefix is set to `" + args[0] + "`");
                } else {
                    builder.setColor(Color.YELLOW);
                    builder.setTitle("Sorry,");
                    builder.setDescription("only the guild owner has the permission to use this command!");
                }
            } else {
                builder.setColor(Color.red);
                builder.setTitle("Sorry,");
                builder.setDescription("you need to set an prefix in the arguments!\nUse: disc0rd/prefixChange <argument>");
            }
            event.getChannel().sendMessage(builder.build()).queue();
        }
        return false;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        helpSite.put("prefixChange <prefix>", "Change the command prefix.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> helpSite = new HashMap<>();
        helpSite.put("prefixChange <prefix>", "Change the command prefix. Admin Command!");
        return helpSite;
    }

    @Override
    public String getCommandName() {
        return "prefixChange";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
    }
}
