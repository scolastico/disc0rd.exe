package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.etc.musicplayer.MusicPlayer;
import com.scolastico.discord_exe.etc.musicplayer.MusicPlayerRegister;
import com.scolastico.discord_exe.etc.permissions.PermissionsManager;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandQueue implements CommandHandler, EventHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {
        if (cmd.equalsIgnoreCase("queue")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.setTitle("Sorry,");
            builder.setDescription("but the command isn't correct. Please check the arguments or try `disc0rd/help queue`.");
            if (args.length == 0) {
                if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "queue")) {
                    if (member.getVoiceState() != null) {
                        MusicPlayer player = MusicPlayerRegister.getInstance().getPlayer(event.getGuild().getIdLong());
                        if (player != null) {
                            builder = loadSite(player, 1);
                        } else {
                            builder.setDescription("There is no player currently. You can start the music player with `disc0rd/play <url>`.");
                        }
                    }
                } else {
                    builder.setDescription("but you dont have the permission to use this command!");
                }
            } else if (args.length == 1) {
                if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "queue")) {
                    if (member.getVoiceState() != null) {
                        MusicPlayer player = MusicPlayerRegister.getInstance().getPlayer(event.getGuild().getIdLong());
                        if (player != null) {
                            try {
                                builder = loadSite(player, Integer.parseInt(args[0]));
                            } catch (NumberFormatException ignored) {}
                        } else {
                            builder.setDescription("There is no player currently. You can start the music player with `disc0rd/play <url>`.");
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

    private EmbedBuilder loadSite(MusicPlayer player, int site) {
        EmbedBuilder builder = new EmbedBuilder();
        if (player.getQueue().size() != 0) {
            ArrayList<AudioTrack> tracks = player.getQueue();
            int sites = 1;
            if (tracks.size()-(tracks.size() % 20) != 0) {
                sites += (tracks.size()-(tracks.size() % 20))/20;
            }
            if (site < 1 || site > sites) {
                builder.setColor(Color.YELLOW);
                builder.setTitle("Sorry,");
                builder.setDescription("but this page does not exist. Please enter a number between 1 and " + sites + ".");
            } else {
                int tmpSite = site-1;
                tmpSite = tmpSite*20;
                ArrayList<AudioTrack> currentSite = new ArrayList<>();
                for (int tmp = 0; tmp != 20 && tmpSite+tmp != tracks.size(); tmp++) {
                    currentSite.add(tracks.get(tmpSite + tmp));
                }
                builder.setColor(Color.GREEN);
                builder.setTitle("Music Player - Current Queue");
                StringBuilder ret = new StringBuilder();
                for (AudioTrack track:currentSite) {
                    ret.append("\n**").append(track.getInfo().title).append("** [").append(track.getInfo().author).append("]");
                }
                builder.setDescription(ret.toString());
                builder.setFooter("Page " + site + " of " + sites + ". To see other pages enter: disc0rd/queue <page>");
            }
        } else {
            builder.setColor(Color.YELLOW);
            builder.setTitle("Sorry,");
            builder.setDescription("but the queue is empty.");
        }
        return builder;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        helpSite.put("queue", "Show the currently music player queue.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("queue", "Show the currently music player queue.");
        ret.put("queue <page>", "Show the currently music player queue.");
        return ret;
    }

    @Override
    public String getCommandName() {
        return "queue";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
        PermissionsManager.getInstance().registerPermission("queue", "Allow a user to use the queue command from the music player.", true);
    }
}
