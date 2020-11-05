package com.scolastico.discord_exe.mysql;

import java.util.ArrayList;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.Invite;

public class ServerStatistics {

  private HashMap<Long, StatisticsJoin> joins = new HashMap<>();
  private HashMap<Long, StatisticsLeft> lefts = new HashMap<>();
  private HashMap<Long, StatisticsMessageActivity> messageActivity = new HashMap<>();
  private HashMap<Long, StatisticsVoiceActivity> voiceActivity = new HashMap<>();
  private StatisticsCustomData customData = new StatisticsCustomData();

  public HashMap<Long, StatisticsJoin> getJoins() {
    return joins;
  }

  public void addJoin(Long time, StatisticsJoin join) {
    joins.put(time, join);
  }

  public void setJoins(HashMap<Long, StatisticsJoin> joins) {
    this.joins = joins;
  }

  public HashMap<Long, StatisticsLeft> getLefts() {
    return lefts;
  }

  public void addLeft(Long time, StatisticsLeft left) {
    lefts.put(time, left);
  }

  public void setLefts(HashMap<Long, StatisticsLeft> lefts) {
    this.lefts = lefts;
  }

  public HashMap<Long, StatisticsMessageActivity> getMessageActivity() {
    return messageActivity;
  }

  public void addMessageActivity(Long time, StatisticsMessageActivity messageActivity) {
    this.messageActivity.put(time, messageActivity);
  }

  public void setMessageActivity(HashMap<Long, StatisticsMessageActivity> messageActivity) {
    this.messageActivity = messageActivity;
  }

  public HashMap<Long, StatisticsVoiceActivity> getVoiceActivity() {
    return voiceActivity;
  }

  public void addVoiceActivity(Long time, StatisticsVoiceActivity voiceActivity) {
    this.voiceActivity.put(time, voiceActivity);
  }

  public void setVoiceActivity(HashMap<Long, StatisticsVoiceActivity> voiceActivity) {
    this.voiceActivity = voiceActivity;
  }

  public StatisticsCustomData getCustomData() {
    return customData;
  }

  public void setCustomData(StatisticsCustomData customData) {
    this.customData = customData;
  }

  public static class StatisticsJoin {
    private final long userId;
    private final long inviterId;
    private final String inviteUrl;

    public StatisticsJoin(
        long userId,
        long inviterId,
        String inviteUrl) {
      this.userId = userId;
      this.inviterId = inviterId;
      this.inviteUrl = inviteUrl;
    }

    public long getUserId() {
      return userId;
    }

    public long getInviterId() {
      return inviterId;
    }

    public String getInviteUrl() {
      return inviteUrl;
    }
  }

  public static class StatisticsLeft {
    private final long userId;
    private final boolean ban;
    private final boolean kick;

    public StatisticsLeft(
        long userId,
        boolean ban,
        boolean kick) {
      this.userId = userId;
      this.ban = ban;
      this.kick = kick;
    }

    public long getUserId() {
      return userId;
    }

    public boolean isBan() {
      return ban;
    }

    public boolean isKick() {
      return kick;
    }
  }

  public static class StatisticsMessageActivity {
    private final long channelId;
    private final long userId;
    private final int length;

    public StatisticsMessageActivity(
        long channelId,
        long userId,
        int length) {
      this.channelId = channelId;
      this.userId = userId;
      this.length = length;
    }

    public long getChannelId() {
      return channelId;
    }

    public long getUserId() {
      return userId;
    }

    public int getLength() {
      return length;
    }
  }

  public static class StatisticsVoiceActivity {
    private final long channelId;
    private final int userCount;
    private final long userId;
    private final boolean joined;

    public StatisticsVoiceActivity(
        long channelId,
        int userCount,
        long userId,
        boolean joined) {
      this.channelId = channelId;
      this.userCount = userCount;
      this.userId = userId;
      this.joined = joined;
    }

    public long getChannelId() {
      return channelId;
    }

    public int getUserCount() {
      return userCount;
    }

    public long getUserId() {
      return userId;
    }

    public boolean isJoined() {
      return joined;
    }
  }

  public static class StatisticsCustomData {
    private ArrayList<Invite> invites = new ArrayList<>();

    public ArrayList<Invite> getInvites() {
      return invites;
    }

    public void setInvites(ArrayList<Invite> invites) {
      this.invites = invites;
    }
  }

}
