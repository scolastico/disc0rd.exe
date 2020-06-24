package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.event.handlers.MessageReceivedHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class OnChatLeaderboard implements EventHandler, MessageReceivedHandler {

    HashMap<Long, Long> timeOut = new HashMap<>();

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerMessageReceivedEvent(this);
    }

    @Override
    public void handleMessageReceived(MessageReceivedEvent messageReceivedEvent) {
        if (messageReceivedEvent.getChannel().getType() == ChannelType.TEXT) {
            User user = messageReceivedEvent.getAuthor();
            if (!user.isFake()) if (!user.isBot()) {
                clearTimeOut();
                if (!timeOut.containsKey(user.getIdLong())) {
                    timeOut.put(user.getIdLong(), getUnixTimeStamp() + 60);
                    ServerSettings settings = Disc0rd.getMysql().getServerSettings(messageReceivedEvent.getGuild().getIdLong());
                    settings.getLeaderboard().addUserXP(user.getIdLong());
                    Disc0rd.getMysql().setServerSettings(messageReceivedEvent.getGuild().getIdLong(), settings);
                }
            }
        }
    }

    private void clearTimeOut() {
        ArrayList<Long> toDelete = new ArrayList<>();
        for (Long time:timeOut.keySet()) {
            if (time < getUnixTimeStamp()) {
                toDelete.add(time);
            }
        }
        for (Long time:toDelete) {
            timeOut.remove(time);
        }
    }

    private Long getUnixTimeStamp() {
        return System.currentTimeMillis() / 1000L;
    }

}