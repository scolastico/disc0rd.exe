package com.scolastico.discord_exe.event.events.commands;

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

public class CommandClearQueue implements EventHandler, CommandHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {
        if (cmd.equalsIgnoreCase("clearQueue")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.setTitle("Sorry,");
            builder.setDescription("but the command isn't correct. Please check the arguments or try `disc0rd/help clearQueue`.");
            if (args.length == 0) {
                if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "clear-queue")) {
                    if (member.getVoiceState() != null) {
                        VoiceChannel channel = member.getVoiceState().getChannel();
                        if (channel != null) {
                            MusicPlayer player = MusicPlayerRegister.getInstance().getPlayer(event.getGuild().getIdLong());
                            if (player != null) {
                                if (player.getChannel() == channel) {
                                    player.clearQueue();
                                    builder.setColor(Color.GREEN);
                                    builder.setTitle("Music Player");
                                    builder.setDescription("Cleared the queue.");
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
            event.getChannel().sendMessage(builder.build()).queue();
            return true;
        }
        return false;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        helpSite.put("clearQueue", "Clear the music player queue.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("clearQueue", "Clear the music player queue.");
        return ret;
    }

    @Override
    public String getCommandName() {
        return "clearQueue";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
        PermissionsManager.getInstance().registerPermission("clear-queue", "Allow a user to use the clearQueue command from the music player.", true);
    }
}
