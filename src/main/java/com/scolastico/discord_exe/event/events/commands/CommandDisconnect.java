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

public class CommandDisconnect implements CommandHandler, EventHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {
        if (cmd.equalsIgnoreCase("disconnect")) {
            Emote emoteNo = EmoteHandler.getInstance().getEmoteNo();
            if (args.length == 0) {
                if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "disconnect")) {
                    if (member.getVoiceState() != null) {
                        VoiceChannel channel = member.getVoiceState().getChannel();
                        if (channel != null) {
                            MusicPlayer player = MusicPlayerRegister.getInstance().getPlayer(event.getGuild().getIdLong());
                            if (player != null) {
                                if (player.getChannel() == channel) {
                                    MusicPlayerRegister.getInstance().killPlayer(event.getGuild().getIdLong());
                                    event.getMessage().addReaction(EmoteHandler.getInstance().getEmoteOk()).queue();
                                } else {
                                    event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but you need to be in the same channel as the bot.").queue();
                                }
                            } else {
                                event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but there is no player currently").queue();
                            }
                        }
                    } else {
                        event.getMessage().addReaction(EmoteHandler.getInstance().getEmoteNoPermission()).queue();
                    }
                }
            } else {
                event.getChannel().sendMessage("<:" + emoteNo.getName() + ":" + emoteNo.getId() + "> Sorry, but i cant find this command. Check your arguments or try `disc0rd/help disconnect`.").queue();
            }
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
