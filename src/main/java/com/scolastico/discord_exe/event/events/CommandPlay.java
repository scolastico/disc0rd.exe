package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.etc.musicplayer.MusicPlayer;
import com.scolastico.discord_exe.etc.musicplayer.MusicPlayerRegister;
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

public class CommandPlay implements CommandHandler, EventHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {
        if (cmd.equalsIgnoreCase("play")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.setTitle("Sorry,");
            builder.setDescription("but i cant find this command. Check your arguments or try `disc0rd/help play`.");
            if (args.length == 0) {
                Member member = event.getGuild().getMember(event.getAuthor());
                if (member != null) {
                    if (member.getVoiceState() != null) {
                        VoiceChannel channel = member.getVoiceState().getChannel();
                        if (channel != null) {
                            MusicPlayer player = MusicPlayerRegister.getInstance().getPlayer(event.getGuild().getIdLong());
                            if (player != null) {
                                if (player.getChannel() == channel) {
                                    if (!player.getStatus()) {
                                        player.play();
                                        builder.setColor(Color.GREEN);
                                        builder.setTitle("Music Player");
                                        builder.setDescription("un- paused.");
                                    } else {
                                        builder.setDescription("the player is not paused.");
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
                }
            } else {
                Member member = event.getGuild().getMember(event.getAuthor());
                if (member != null) {
                    if (member.getVoiceState() != null) {
                        VoiceChannel channel = member.getVoiceState().getChannel();
                        if (channel != null) {
                            MusicPlayer player = MusicPlayerRegister.getInstance().getPlayer(event.getGuild().getIdLong());
                            if (player == null) {
                                player = new MusicPlayer(channel, event.getTextChannel());
                            }
                            if (player.getChannel() == channel) {
                                StringBuilder arg = new StringBuilder();
                                for (String tmp:args) {
                                    arg.append(tmp).append(" ");
                                }
                                player.addToQueue(arg.substring(0, arg.length()-1));
                                return true;
                            }
                            builder.setDescription("but you need to be in the same channel as the bot.");
                        } else {
                            builder.setDescription("but you need to be in the an voice channel to do that.");
                        }
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
        helpSite.put("play", "Play a song in your voice channel.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("play <url>", "Play an specified song from an link.");
        ret.put("play <song name>", "Try to search the song and play it.");
        ret.put("play <playlist url>", "Add an complete playlist to the queue and play it.");
        return ret;
    }

    @Override
    public String getCommandName() {
        return "play";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
    }
}
