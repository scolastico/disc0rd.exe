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
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;

public class CommandPause implements CommandHandler, EventHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {
        if (cmd.equalsIgnoreCase("pause")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.setTitle("Sorry,");
            builder.setDescription("but the command isn't correct. Please check the arguments or try `disc0rd/help pause`.");
            if (args.length == 0) {
                Member member = event.getGuild().getMember(event.getAuthor());
                if (member != null) {
                    if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "pause")) {
                        if (member.getVoiceState() != null) {
                            VoiceChannel channel = member.getVoiceState().getChannel();
                            if (channel != null) {
                                MusicPlayer player = MusicPlayerRegister.getInstance().getPlayer(event.getGuild().getIdLong());
                                if (player != null) {
                                    if (player.getChannel() == channel) {
                                        if (player.getStatus()) {
                                            player.pause();
                                            builder.setColor(Color.YELLOW);
                                            builder.setTitle("Music Player");
                                            builder.setDescription("paused.");
                                        } else {
                                            player.play();
                                            builder.setColor(Color.GREEN);
                                            builder.setTitle("Music Player");
                                            builder.setDescription("Un- paused.");
                                        }
                                    } else {
                                        builder.setDescription("but you need to be in the same channel as the bot.");
                                    }
                                } else {
                                    builder.setDescription("There is no player currently. You can start the music player with `disc0rd/play <url>`.");
                                }
                            } else {
                                builder.setDescription("but you need to be in the an voice channel to do that.");
                            }
                        }
                    } else {
                        builder.setDescription("but you dont have the permission to use this command!");
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
        helpSite.put("pause", "Pause the music player.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("pause", "Pause the music player.");
        return ret;
    }

    @Override
    public String getCommandName() {
        return "pause";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
        PermissionsManager.getInstance().registerPermission("pause", "Allow a user to use the pause command from the music player.", true);
    }
}
