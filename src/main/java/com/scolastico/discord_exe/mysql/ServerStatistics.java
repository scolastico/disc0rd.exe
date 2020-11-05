package com.scolastico.discord_exe.mysql;

import com.scolastico.discord_exe.etc.Tools;
import java.util.ArrayList;
import java.util.HashMap;
import net.dv8tion.jda.api.entities.Invite;

public class ServerStatistics {

  private HashMap<Long, ArrayList<StatisticsJoin>> joins = new HashMap<>();
  private HashMap<Long, ArrayList<StatisticsLeft>> lefts = new HashMap<>();
  private HashMap<Long, ArrayList<StatisticsMessageActivity>> messageActivity = new HashMap<>();
  private HashMap<Long, ArrayList<StatisticsVoiceActivity>> voiceActivity = new HashMap<>();
  private StatisticsCustomData customData = new StatisticsCustomData();

  public HashMap<Long, ArrayList<StatisticsJoin>> getJoins() {
    return joins;
  }

  public void addJoin(StatisticsJoin join) {
    addToHashMap(joins, join);
  }

  public void setJoins(HashMap<Long, ArrayList<StatisticsJoin>> joins) {
    this.joins = joins;
  }

  public HashMap<Long, ArrayList<StatisticsLeft>> getLefts() {
    return lefts;
  }

  public void addLeft(StatisticsLeft left) {
    addToHashMap(lefts, left);
  }

  public void setLefts(HashMap<Long, ArrayList<StatisticsLeft>> lefts) {
    this.lefts = lefts;
  }

  public HashMap<Long, ArrayList<StatisticsMessageActivity>> getMessageActivity() {
    return messageActivity;
  }

  public void addMessageActivity(StatisticsMessageActivity messageActivity) {
    addToHashMap(this.messageActivity, messageActivity);
  }

  public void setMessageActivity(HashMap<Long, ArrayList<StatisticsMessageActivity>> messageActivity) {
    this.messageActivity = messageActivity;
  }

  public HashMap<Long, ArrayList<StatisticsVoiceActivity>> getVoiceActivity() {
    return voiceActivity;
  }

  public void addVoiceActivity(StatisticsVoiceActivity voiceActivity) {
    addToHashMap(this.voiceActivity, voiceActivity);
  }

  public void setVoiceActivity(HashMap<Long, ArrayList<StatisticsVoiceActivity>> voiceActivity) {
    this.voiceActivity = voiceActivity;
  }

  public StatisticsCustomData getCustomData() {
    return customData;
  }

  public void setCustomData(StatisticsCustomData customData) {
    this.customData = customData;
  }

  private <T> void addToHashMap(HashMap<Long, ArrayList<T>> hashMap, T obj) {
    Long time = Tools.getInstance().getUnixTimeStamp();
    ArrayList<T> list;
    if (hashMap.containsKey(time)) {
      list = hashMap.get(time);
    } else {
      list = new ArrayList<>();
    }
    list.add(obj);
    hashMap.remove(time);
    hashMap.put(time, list);
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
