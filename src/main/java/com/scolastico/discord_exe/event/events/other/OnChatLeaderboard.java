package com.scolastico.discord_exe.event.events.other;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.etc.permissions.PermissionsManager;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.event.handlers.MessageReceivedHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class OnChatLeaderboard implements EventHandler, MessageReceivedHandler {

    HashMap<Long, Long> timeOut = new HashMap<>();

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerMessageReceivedEvent(this);
        PermissionsManager.getInstance().registerPermission("leaderboard-collect-text", "Allow a user to collect points on the leaderboard via text chat.", true);
    }

    @Override
    public void handleMessageReceived(MessageReceivedEvent messageReceivedEvent) {
        if (messageReceivedEvent.getChannel().getType() == ChannelType.TEXT) {
            User user = messageReceivedEvent.getAuthor();
            Member member = messageReceivedEvent.getGuild().getMember(user);
            if (member == null) return;
            if (!user.isBot()) if (PermissionsManager.getInstance().checkPermission(messageReceivedEvent.getGuild(), member, "leaderboard-collect-text")){
                clearTimeOut();
                if (!timeOut.containsKey(user.getIdLong())) {
                    timeOut.put(user.getIdLong(), Tools.getInstance().getUnixTimeStamp() + 60);
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
            if (time < Tools.getInstance().getUnixTimeStamp()) {
                toDelete.add(time);
            }
        }
        for (Long time:toDelete) {
            timeOut.remove(time);
        }
    }

}
