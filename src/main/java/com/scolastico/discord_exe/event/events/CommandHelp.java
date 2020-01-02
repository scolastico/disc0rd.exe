package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;


public class CommandHelp implements EventHandler, CommandHandler {

    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {

        if(cmd.equalsIgnoreCase("disc0rd/help")) {
            if(args.length == 0) {
                event.getMessage().delete().queue();

                EmbedBuilder message = new EmbedBuilder();

                message.setColor(Color.green);
                message.setTitle("Disc0rd.exe | Command Help");
                message.addField("disc0rd/help", "Shows this message.", false);

                event.getChannel().sendMessage(message.build()).queue();
            }

            return true;
        }

        return false;
    }

    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
    }
}
