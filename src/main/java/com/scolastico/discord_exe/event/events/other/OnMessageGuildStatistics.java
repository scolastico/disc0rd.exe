package com.scolastico.discord_exe.event.events.other;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.GuildStatisticsTools;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.event.handlers.MessageReceivedHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import com.scolastico.discord_exe.mysql.ServerStatistics;
import com.scolastico.discord_exe.mysql.ServerStatistics.StatisticsMessageActivity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class OnMessageGuildStatistics implements EventHandler, MessageReceivedHandler {
  @Override
  public void registerEvents(EventRegister eventRegister) {
    eventRegister.registerMessageReceivedEvent(this);
  }

  @Override
  public void handleMessageReceived(MessageReceivedEvent messageReceivedEvent) {
    try {
      if (messageReceivedEvent.getChannelType().isGuild()) {
        ServerSettings settings = Disc0rd.getMysql().getServerSettings(messageReceivedEvent.getGuild().getIdLong());
        ServerStatistics statistics = settings.getStatistics();
        ServerStatistics.StatisticsMessageActivity activity = new StatisticsMessageActivity(
            messageReceivedEvent.getChannel().getIdLong(),
            messageReceivedEvent.getAuthor().getIdLong(),
            messageReceivedEvent.getMessage().getContentRaw().length()
        );
        statistics.addMessageActivity(activity);
        statistics = GuildStatisticsTools.getInstance().deleteOldStatisticData(statistics);
        settings.setStatistics(statistics);
        Disc0rd.getMysql().setServerSettings(messageReceivedEvent.getGuild().getIdLong(), settings);
      }
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
  }
}
