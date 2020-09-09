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

public class CommandSkip implements EventHandler, CommandHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {
        if (cmd.equalsIgnoreCase("skip")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.setTitle("Sorry,");
            builder.setDescription("but the command isn't correct. Please check the arguments or try `disc0rd/help skip`.");
            if (args.length == 0) {
                Member member = event.getGuild().getMember(event.getAuthor());
                if (member != null) {
                    if (member.getVoiceState() != null) {
                        VoiceChannel channel = member.getVoiceState().getChannel();
                        if (channel != null) {
                            MusicPlayer player = MusicPlayerRegister.getInstance().getPlayer(event.getGuild().getIdLong());
                            if (player != null) {
                                if (player.getChannel() == channel) {
                                    player.nextQueueSong();
                                    builder.setColor(Color.YELLOW);
                                    builder.setTitle("Music Player");
                                    builder.setDescription("Track skipped.");
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
            }
            event.getChannel().sendMessage(builder.build()).queue();
            return true;
        }
        return false;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        helpSite.put("skip", "Skip the currently played track.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("skip", "Skip the currently played track.");
        return ret;
    }

    @Override
    public String getCommandName() {
        return "skip";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
    }
}
