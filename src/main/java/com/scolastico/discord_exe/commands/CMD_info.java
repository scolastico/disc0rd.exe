package com.scolastico.discord_exe.commands;

import com.scolastico.discord_exe.Disc0rd;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CMD_info implements CommandHandler {

    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {
        if (cmd.equalsIgnoreCase("disc0rd/info") && args.length == 0) {
            event.getMessage().delete().queue();

            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("Disc0rd.exe - Tool Bot for everyone!");
            builder.setDescription(
                    "Version " + Disc0rd.getVersion()
            );

            event.getChannel().sendMessage(builder.build()).queue();
        }
        return false;
    }

}
