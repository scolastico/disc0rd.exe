package com.scolastico.discord_exe.event.events.other;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.event.handlers.GuildJoinHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

import java.awt.*;

public class OnGuildJoin implements EventHandler, GuildJoinHandler {
    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerGuildJoinEvent(this);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        TextChannel channel = event.getGuild().getDefaultChannel();
        if (channel != null) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.CYAN);
            builder.setTitle("Disc0rd.exe");
            builder.setDescription("Thank you for inviting me to your guild.\nTo start with, it is best to execute `disc0rd/help`, otherwise you could change the prefix of the commands with `disc0rd/prefixChange`.");
            builder.setFooter("Disc0rd.exe made with <3 by scolasti.co");
            channel.sendMessage(builder.build()).queue();
        }
    }
}
