package com.scolastico.discord_exe.commands;

import com.scolastico.discord_exe.etc.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class CMD_help implements CommandHandler {

    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {

        if(cmd.equalsIgnoreCase("disc0rd/help")) {

            if(args.lenght == 0) {
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
}
