package com.scolastico.discord_exe.event.events.commands;

import com.scolastico.discord_exe.etc.EmoteHandler;
import com.scolastico.discord_exe.etc.musicplayer.MusicPlayer;
import com.scolastico.discord_exe.etc.musicplayer.MusicPlayerRegister;
import com.scolastico.discord_exe.etc.permissions.PermissionsManager;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;

public class CommandPlay implements CommandHandler, EventHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {
        if (cmd.equalsIgnoreCase("play")) {
            Emote emoteNo = EmoteHandler.getInstance().getEmoteNo();
            if (args.length == 0) {
                if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "play")) {
                    if (member.getVoiceState() != null) {
                        VoiceChannel channel = member.getVoiceState().getChannel();
                        if (channel != null) {
                            MusicPlayer player = MusicPlayerRegister.getInstance().getPlayer(event.getGuild().getIdLong());
                            if (player != null) {
                                if (player.getChannel() == channel) {
                                    if (!player.getStatus()) {
                                        player.play();
                                        event.getMessage().addReaction(EmoteHandler.getInstance().getEmotePlay()).queue();
                                    } else {
                                        event.getMessage().addReaction(EmoteHandler.getInstance().getEmoteNo()).queue();
                                    }
                                } else {
                                    event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but you need to be in the same voice channel as the bot.").queue();
                                }
                            } else {
                                event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but there is no player currently. You can start the music player with `disc0rd/play <url>`.").queue();
                            }
                        } else {
                            event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but you need to be in a voice channel.").queue();
                        }
                    }
                } else {
                    event.getMessage().addReaction(EmoteHandler.getInstance().getEmoteNoPermission()).queue();
                }
            } else {
                if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "play")) {
                    if (member.getVoiceState() != null) {
                        VoiceChannel channel = member.getVoiceState().getChannel();
                        if (channel != null) {
                            MusicPlayer player = MusicPlayerRegister.getInstance().getPlayer(event.getGuild().getIdLong());
                            if (player == null) {
                                player = new MusicPlayer(channel, event.getTextChannel());
                            }
                            if (player.getChannel() == channel) {
                                player.setTextChannel(event.getTextChannel());
                                StringBuilder arg = new StringBuilder();
                                for (String tmp:args) {
                                    arg.append(tmp).append(" ");
                                }
                                player.addToQueue(arg.substring(0, arg.length()-1));
                                return true;
                            }
                            event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but you need to be in the same voice channel as the bot.").queue();
                        } else {
                            event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but you need to be in a voice channel.").queue();
                        }
                    }
                } else {
                    event.getMessage().addReaction(EmoteHandler.getInstance().getEmoteNoPermission()).queue();
                }
            }
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
        PermissionsManager.getInstance().registerPermission("play", "Allow a user to use the play command from the music player.", true);
    }
}
