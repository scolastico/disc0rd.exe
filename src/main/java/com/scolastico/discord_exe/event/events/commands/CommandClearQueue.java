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

public class CommandClearQueue implements EventHandler, CommandHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {
        if (cmd.equalsIgnoreCase("clearQueue")) {
            Emote emoteNo = EmoteHandler.getInstance().getEmoteNo();
            if (args.length == 0) {
                if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "clear-queue")) {
                    if (member.getVoiceState() != null) {
                        VoiceChannel channel = member.getVoiceState().getChannel();
                        if (channel != null) {
                            MusicPlayer player = MusicPlayerRegister.getInstance().getPlayer(event.getGuild().getIdLong());
                            if (player != null) {
                                if (player.getChannel() == channel) {
                                    player.setTextChannel(event.getTextChannel());
                                    player.clearQueue();
                                    event.getMessage().addReaction(EmoteHandler.getInstance().getEmoteOk()).queue();
                                } else {
                                    event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but you need to be in the same voice channel as the bot.").queue();
                                }
                            } else {
                                event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but there is no player currently. You can start the music player with `disc0rd/play <url>`.").queue();
                            }
                        } else {
                            event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but you need to be in the same voice channel as the bot.").queue();
                        }
                    }
                } else {
                    event.getMessage().addReaction(EmoteHandler.getInstance().getEmoteNoPermission()).queue();
                }
            } else {
                event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but i cant find this command. Check your arguments or try `disc0rd/help clearQueue`.").queue();
            }
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
