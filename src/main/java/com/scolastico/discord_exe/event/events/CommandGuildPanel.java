package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.webserver.context.GuildPanel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class CommandGuildPanel implements EventHandler, CommandHandler {


    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {
        if (cmd.equalsIgnoreCase("disc0rd/admin")) {
            event.getMessage().delete().queue();
            EmbedBuilder embedBuilder = new EmbedBuilder();
            if (Tools.getInstance().isOwner(event.getMessage().getGuild(), event.getAuthor())) {
                event.getAuthor().openPrivateChannel().complete().sendMessage("You can login here: <" + Disc0rd.getConfig().getWebServer().getDomain() + "admin/login.html#" + GuildPanel.getAuthToken(event.getGuild().getIdLong()) + ">").queue();
                embedBuilder.setTitle("Success,");
                embedBuilder.setDescription("i send you a private message!");
                embedBuilder.setColor(Color.green);
            } else {
                embedBuilder.setTitle("Sorry,");
                embedBuilder.setDescription("but only the guild owner has the permission to use this command!");
                embedBuilder.setColor(Color.red);
            }
            event.getChannel().sendMessage(embedBuilder.build()).queue();
        }
        return false;
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
    }
}
