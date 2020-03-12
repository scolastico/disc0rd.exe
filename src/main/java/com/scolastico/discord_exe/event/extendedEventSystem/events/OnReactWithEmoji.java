package com.scolastico.discord_exe.event.extendedEventSystem.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEvent;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventDataStore;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventManager;
import com.scolastico.discord_exe.event.extendedEventSystem.interfaces.Disc0rdEvent;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.event.handlers.MessageReactionAddHandler;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.HashMap;

public class OnReactWithEmoji implements Disc0rdEvent, MessageReactionAddHandler, EventHandler {
    @Override
    public void registerDisc0rdEvent() {
        Disc0rd.getEventRegister().registerMessageReactionAddEvent(this);
    }

    @Override
    public String getName() {
        return "On react with emoji";
    }

    @Override
    public String getDescription() {
        return "Triggers if an member react with an emoji under a message\n\n" +
                "The message is saved in 'event-message',\n" +
                "the message id is saved in 'event-message-id',\n" +
                "the reacting user name is saved in 'event-react-user-name',\n" +
                "the reacting user id is saved in 'event-react-user-id',\n" +
                "the message sender name is saved in 'event-message-author-name',\n" +
                "the message sender id is saved in 'event-message-author-id',\n" +
                "the emoji name is saved in 'event-emoji-name',\n" +
                "the emoji id is saved in 'event-emoji-id',\n" +
                "the channel id is saved in 'event-channel-id',\n" +
                "the channel name is saved in 'event-channel-name'.";
    }

    @Override
    public HashMap<String, String> getConfig() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("Message ID", "Enter a message id here if you want the bot to only\n" +
                "pay attention to a defined message or leave this field empty so\n" +
                "that it pays attention to all messages.");
        ret.put("Emote ID", "Enter an emote ID here if you want the bot to only pay\n" +
                "attention to a defined emote or leave this field empty so that it\n" +
                "pays attention to all emotes.");
        return ret;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getChannelType() == ChannelType.TEXT) {
            String messageId = event.getReaction().getMessageId();
            String emoteId = event.getReaction().getReactionEmote().getId();
            Message message = event.getTextChannel().retrieveMessageById(event.getMessageId()).complete();
            if (message == null) return;
            for (ExtendedEvent extendedEvent: ExtendedEventManager.getInstance().getExtendedEvents(event.getGuild().getIdLong())) {
                if (extendedEvent.getEvent().equals(getName())) {
                    if (extendedEvent.getEventConfig().getOrDefault("Message ID", "").equals("") || extendedEvent.getEventConfig().getOrDefault("Message ID", "").equals(messageId)) {
                        if (extendedEvent.getEventConfig().getOrDefault("Emote ID", "").equals("") || extendedEvent.getEventConfig().getOrDefault("Emote ID", "").equals(emoteId)) {
                            ExtendedEventDataStore dataStore = new ExtendedEventDataStore(extendedEvent);
                            User user = event.getUser();
                            if (user == null) {
                                ErrorHandler.getInstance().handle(new Exception("User is null?"));
                                continue;
                            }
                            dataStore.setDataStore("event-message", message.getContentRaw());
                            dataStore.setDataStore("event-message-id", event.getMessageId());
                            dataStore.setDataStore("event-react-user-name", user.getName());
                            dataStore.setDataStore("event-react-user-id", event.getUserId());
                            dataStore.setDataStore("event-message-author-name", message.getAuthor().getName());
                            dataStore.setDataStore("event-message-author-id", message.getAuthor().getId());
                            dataStore.setDataStore("event-emoji-name", event.getReaction().getReactionEmote().getName());
                            dataStore.setDataStore("event-emoji-id", event.getReaction().getReactionEmote().getId());
                            dataStore.setDataStore("event-channel-name", event.getChannel().getName());
                            dataStore.setDataStore("event-channel-id", event.getChannel().getId());
                            ExtendedEventManager.getInstance().executeAction(dataStore);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {}
}
