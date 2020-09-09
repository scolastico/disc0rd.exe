package com.scolastico.discord_exe.etc.musicplayer;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.ScheduleTask;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.event.handlers.ScheduleHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class MusicPlayerRegister {

    private final HashMap<Long, MusicPlayer> players = new HashMap<>();
    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    private static MusicPlayerRegister instance = null;

    private MusicPlayerRegister() {
        ScheduleTask.getInstance().runScheduledTaskRepeat(new Runnable() {
            @Override
            public void run() {
                try {
                    int afkTime = Disc0rd.getConfig().getMusicPlayerTimout();
                    ArrayList<Long> toRemove = new ArrayList<>();
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.YELLOW);
                    builder.setTitle("Hey,");
                    builder.setDescription("i disconnected because i was afk for more than " + afkTime + " seconds.");
                    for(long id:players.keySet()) {
                        try {
                            MusicPlayer player = players.get(id);
                            player.scheduler();
                            if (player.getIdleTime() >= afkTime) {
                                player.kill();
                                if (player.isSendMessage()) {
                                    player.getTextChannel().sendMessage(builder.build()).queue();
                                }
                                toRemove.add(id);
                            }
                        } catch (Exception e) {
                            ErrorHandler.getInstance().handle(e);
                        }
                    }
                    for (long id:toRemove) {
                        players.remove(id);
                    }
                } catch (Exception e) {
                    ErrorHandler.getInstance().handle(e);
                }
            }
        }, 20, 20, true);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    public static MusicPlayerRegister getInstance() {
        if (instance == null) {
            instance = new MusicPlayerRegister();
        }
        return instance;
    }


    public void registerMusicPlayer(MusicPlayer player) {
        players.put(player.getChannel().getGuild().getIdLong(), player);
    }

    public MusicPlayer getPlayer(long id) {
        if (players.containsKey(id)) return players.get(id);
        return null;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public void killPlayer(long id) {
        if (players.containsKey(id)) {
            players.get(id).kill();
            players.remove(id);
        }
    }

}
