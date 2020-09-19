package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.webserver.context.OwnerPanel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;

public class CommandOwnerPanel implements EventHandler, CommandHandler {

    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {
        if (cmd.equalsIgnoreCase("owner-panel") && args.length == 0) {
            event.getMessage().delete().queue();
            if (event.getMessage().getAuthor().getIdLong() == Disc0rd.getConfig().getOwnerPanel().getOwnerId()) {
                event.getAuthor().openPrivateChannel().complete().sendMessage("You can login here: <" + Disc0rd.getConfig().getWebServer().getDomain() + "owner-panel/login.html#" + OwnerPanel.getAuthCode() + ">").queue();
            } else {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.red);
                embedBuilder.setTitle("Sorry,");
                embedBuilder.setDescription("but this is an system internal command!");
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
            return true;
        }
        return false;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        return new HashMap<>();
    }

    @Override
    public String getCommandName() {
        return null;
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
    }
}
