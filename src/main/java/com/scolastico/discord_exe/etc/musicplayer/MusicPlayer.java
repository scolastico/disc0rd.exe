package com.scolastico.discord_exe.etc.musicplayer;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class MusicPlayer {

  public MusicPlayer(VoiceChannel channel, TextChannel textChannel) {
    this.channel = channel;
    this.textChannel = textChannel;
    MusicPlayerRegister.getInstance().registerMusicPlayer(this);
    player =
        MusicPlayerRegister.getInstance().getPlayerManager().createPlayer();
    playerManager = MusicPlayerRegister.getInstance().getPlayerManager();
    guild = channel.getGuild();
    guild.getAudioManager().openAudioConnection(channel);
    guild.getAudioManager().setSendingHandler(
        new AudioPlayerSendHandler(player));
    member = guild.getMember(Disc0rd.getJda().getSelfUser());
    assert member != null;
  }

  private final Member member;
  private final Guild guild;
  private final VoiceChannel channel;
  private TextChannel textChannel;
  private int idleTime = -10;
  private ArrayList<AudioTrack> queue = new ArrayList<>();
  private boolean status = false;
  private final AudioPlayer player;
  private final AudioPlayerManager playerManager;
  private boolean sendMessage = true;

  public boolean isSendMessage() { return sendMessage; }

  public void addToQueue(AudioTrack track) { queue.add(track); }

  public void clearQueue() { queue = new ArrayList<>(); }

  public void nextQueueSong() {
    if (queue.size() != 0) {
      AudioTrack track = queue.get(0);
      queue.remove(0);
      playInstantly(track);
    } else {
      player.stopTrack();
    }
  }

  public ArrayList<AudioTrack> getQueue() { return queue; }

  public void playInstantly(AudioTrack track) { player.playTrack(track); }

  public void addToQueue(String trackUrl) {
    try {
      if (!(trackUrl.startsWith("http") || trackUrl.startsWith("ytsearch:") ||
            trackUrl.startsWith("scsearch:"))) {
        trackUrl = "ytsearch:" + trackUrl;
      }
      Pattern pattern = Pattern.compile(
          "^https?:\\/\\/open\\.spotify\\.com\\/(track|playlist|album)\\/([0-9a-z]+)\\?",
          Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(trackUrl);
      if (matcher.find()) {
        String id = matcher.group(2);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setTitle("Sorry,");
        builder.setDescription(
            "but i have trouble while converting this song/playlist to YouTube.");
        if (matcher.group(1).equalsIgnoreCase("playlist")) {
          String[] urls =
              SpotifyToYoutube.getInstance().spotifyPlaylistToString(id);
          if (urls.length == 0) {
            textChannel.sendMessage(builder.build()).queue();
          } else {
            for (String url : urls) {
              addToQueue(url);
            }
          }
          return;
        } else if (matcher.group(1).equalsIgnoreCase("track")) {
          trackUrl = SpotifyToYoutube.getInstance().spotifyToString(id);
          if (trackUrl == null) {
            textChannel.sendMessage(builder.build()).queue();
            return;
          }
        } else if (matcher.group(1).equalsIgnoreCase("album")) {
          String[] urls =
              SpotifyToYoutube.getInstance().spotifyAlbumToString(id);
          if (urls.length == 0) {
            textChannel.sendMessage(builder.build()).queue();
          } else {
            for (String url : urls) {
              addToQueue(url);
            }
          }
          return;
        }
      }
      if (idleTime > 0)
        idleTime = 0;
      playerManager.loadItemOrdered(this, trackUrl, new AudioLoadResultHandler() {
        @Override
        public void trackLoaded(AudioTrack track) {
          EmbedBuilder builder = new EmbedBuilder();
          builder.setColor(Color.GREEN);
          builder.setTitle("Added Song to Queue");
          builder.setDescription("**" + track.getInfo().title + "** [" +
                                 track.getInfo().author + "]");
          textChannel.sendMessage(builder.build()).queue();
          addToQueue(track);
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
          AudioTrack firstTrack = playlist.getSelectedTrack();
          if (firstTrack == null) {
            firstTrack = playlist.getTracks().get(0);
          }
          addToQueue(firstTrack);
          EmbedBuilder builder = new EmbedBuilder();
          builder.setColor(Color.GREEN);
          if (playlist.isSearchResult()) {
            builder.setTitle("Added Song to Queue");
            builder.setDescription("**" + firstTrack.getInfo().title + "** [" +
                                   firstTrack.getInfo().author + "]");
          } else {
            builder.setTitle("Added Playlist to Queue");
            builder.setDescription("**" + playlist.getName() + "** (" +
                                   playlist.getTracks().size() + " Tracks)");
            for (AudioTrack track : playlist.getTracks()) {
              if (firstTrack != track) {
                addToQueue(track);
              }
            }
          }
          textChannel.sendMessage(builder.build()).queue();
        }

        @Override
        public void noMatches() {
          EmbedBuilder builder = new EmbedBuilder();
          builder.setColor(Color.RED);
          builder.setTitle("Sorry,");
          builder.setDescription("but i can't find this song.");
          textChannel.sendMessage(builder.build()).queue();
        }

        @Override
        public void loadFailed(FriendlyException exception) {
          EmbedBuilder builder = new EmbedBuilder();
          builder.setColor(Color.RED);
          builder.setTitle("Sorry,");
          builder.setDescription(
              "but there was an exception while trying to play this song: `" +
              exception.getMessage() + "`");
          textChannel.sendMessage(builder.build()).queue();
        }
      });
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
  }

  public TextChannel getTextChannel() { return textChannel; }

  public VoiceChannel getChannel() { return channel; }

  public void setTextChannel(TextChannel channel) { textChannel = channel; }

  public void pause() { player.setPaused(true); }

  public void play() { player.setPaused(false); }

  public void scheduler() {
    if (idleTime < 0) {
      idleTime++;
      return;
    }
    status = !player.isPaused() && player.getPlayingTrack() != null;
    if (!guild.getAudioManager().isConnected() ||
        member.getVoiceState() == null) {
      pause();
      idleTime = Disc0rd.getConfig().getMusicPlayerTimout();
      sendMessage = false;
    } else {
      if (!member.getVoiceState().isDeafened()) {
        try {
          member.deafen(true).complete();
          EmbedBuilder builder = new EmbedBuilder();
          builder.setColor(Color.RED);
          builder.setTitle("Hey,");
          builder.setDescription(
              "please dont un- deafen me. This is required to save resources.\nOr do you want to pay my server bill?");
          textChannel.sendMessage(builder.build()).queue();
        } catch (Exception ignored) {
        }
      }
      if (channel.getMembers().size() == 1) {
        pause();
        idleTime = Disc0rd.getConfig().getMusicPlayerTimout();
        sendMessage = false;
      }
    }
    if (status) {
      idleTime = 0;
    } else {
      idleTime++;
    }
    if (player.getPlayingTrack() == null) {
      nextQueueSong();
    }
  }

  public int getIdleTime() { return idleTime; }

  public boolean getStatus() { return status; }

  public void kill() {
    player.stopTrack();
    guild.getAudioManager().closeAudioConnection();
  }
}
