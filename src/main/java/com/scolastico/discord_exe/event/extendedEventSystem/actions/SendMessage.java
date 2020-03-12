package com.scolastico.discord_exe.event.extendedEventSystem.actions;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventDataStore;
import com.scolastico.discord_exe.event.extendedEventSystem.interfaces.Disc0rdAction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.util.HashMap;

public class SendMessage implements Disc0rdAction {

    @Override
    public ExtendedEventDataStore doAction(ExtendedEventDataStore dataStore, Integer idFromAction) {
        try {
            HashMap<String, String> config = dataStore.getExtendedEvent().getActions().get(idFromAction).getConfig();
            Guild guild = Disc0rd.getJda().getGuildById(dataStore.getExtendedEvent().getGuild());
            if (guild == null) {
                Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[Send Message] [" + idFromAction + "] Guild not found!");
                dataStore.setCancelled(true);
                return dataStore;
            }
            TextChannel textChannel = guild.getTextChannelById(Tools.getInstance().getStringWithVarsFromDataStore(dataStore, config.getOrDefault("Channel", "0")));
            if (textChannel == null) {
                Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[Send Message] [" + idFromAction + "] Text Channel not found!");
                dataStore.setCancelled(true);
                return dataStore;
            }
            if (textChannel.getGuild().getIdLong() != guild.getIdLong()) {
                Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[Send Message] [" + idFromAction + "] Text Channel not from this guild!");
                dataStore.setCancelled(true);
                return dataStore;
            }
            String message = Tools.getInstance().getStringWithVarsFromDataStore(dataStore, config.getOrDefault("Message", ""));
            if (message.isEmpty()) {
                Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[Send Message] [" + idFromAction + "] Message is empty!");
                dataStore.setCancelled(true);
                return dataStore;
            }
            String id = textChannel.sendMessage(message).complete().getId();
            dataStore.setDataStore("action-" + idFromAction + "-message", message);
            dataStore.setDataStore("action-" + idFromAction + "-message-id", id);
        } catch (InsufficientPermissionException e) {
            Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[Send Message] [" + idFromAction + "] No permission to send message!");
            dataStore.setCancelled(true);
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
            Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[Send Message] [" + idFromAction + "] Internal Error Occurred!");
            dataStore.setCancelled(true);
        }
        return dataStore;
    }

    @Override
    public String getName() {
        return "Send Chat Message";
    }

    @Override
    public String getDescription() {
        return "Sends an Chat Message to an Channel. \n\n" +
                "The message is saved in 'action-{id}-message' and \n" +
                "the message id is saved in 'action-{id}-message-id'.";
    }

    @Override
    public HashMap<String, String> getConfig() {
        HashMap<String, String> config = new HashMap<>();
        config.put("Message", "The message to be sent.");
        config.put("Channel", "The channel ID in which the message should be sent.");
        return config;
    }

}
