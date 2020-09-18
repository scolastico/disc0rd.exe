package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.etc.musicplayer.MusicPlayer;
import com.scolastico.discord_exe.etc.musicplayer.MusicPlayerRegister;
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

public class CommandDisconnect implements CommandHandler, EventHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {
        if (cmd.equalsIgnoreCase("disconnect")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.setTitle("Sorry,");
            builder.setDescription("but the command isn't correct. Please check the arguments or try `disc0rd/help disconnect`.");
            if (args.length == 0) {
                if (member.getVoiceState() != null) {
                    MusicPlayer player = MusicPlayerRegister.getInstance().getPlayer(event.getGuild().getIdLong());
                    if (player != null) {
                        MusicPlayerRegister.getInstance().killPlayer(event.getGuild().getIdLong());
                        builder.setColor(Color.YELLOW);
                        builder.setTitle("Music Player");
                        builder.setDescription("Disconnected...");
                    } else {
                        builder.setDescription("There is no player currently.");
                    }
                }
            }
            event.getChannel().sendMessage(builder.build()).queue();
            return true;
        }
        return false;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        helpSite.put("disconnect", "Stop the music player and disconnect from the voice channel.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("disconnect", "Stop the music player and disconnect from the voice channel.");
        return ret;
    }

    @Override
    public String getCommandName() {
        return "disconnect";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
        PermissionsManager.getInstance().registerPermission("disconnect", "Allow a user to use the disconnect command from the music player.", true);
    }
}
