package com.scolastico.discord_exe.etc;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.config.ConfigDataStore;
import com.scolastico.discord_exe.mysql.ServerStatistics;
import java.util.ArrayList;
import java.util.HashMap;

public class GuildStatisticsTools {

  private static GuildStatisticsTools instance = null;

  public static GuildStatisticsTools getInstance() {
    if (instance == null) {
      instance = new GuildStatisticsTools();
    }
    return instance;
  }

  private GuildStatisticsTools() {}

  public ServerStatistics deleteOldStatisticData(ServerStatistics statistics) {
    try {
      ConfigDataStore.Statistics config = Disc0rd.getConfig().getStatistics();
      statistics.setJoins(deleteOldFromHashMap(statistics.getJoins(), config.getDeleteJoins()));
      statistics.setLefts(deleteOldFromHashMap(statistics.getLefts(), config.getDeleteLefts()));
      statistics.setMessageActivity(deleteOldFromHashMap(statistics.getMessageActivity(), config.getDeleteMessageActivity()));
      statistics.setVoiceActivity(deleteOldFromHashMap(statistics.getVoiceActivity(), config.getDeleteVoiceActivity()));
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
    return statistics;
  }

  private <T> HashMap<Long, T> deleteOldFromHashMap(HashMap<Long, T> hashMap, int days) {
    ArrayList<Long> toDelete = new ArrayList<>();
    for (Long time:hashMap.keySet()) {
      if ((Tools.getInstance().getUnixTimeStamp() - time) >= (days*24*60*60)) {
        toDelete.add(time);
      }
    }
    for (Long time:toDelete) {
      hashMap.remove(time);
    }
    return hashMap;
  }

}
