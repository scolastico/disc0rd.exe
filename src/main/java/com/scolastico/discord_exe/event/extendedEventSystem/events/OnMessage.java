package com.scolastico.discord_exe.event.extendedEventSystem.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEvent;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventDataStore;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventManager;
import com.scolastico.discord_exe.event.extendedEventSystem.interfaces.Disc0rdEvent;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.event.handlers.MessageReceivedHandler;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;

public class OnMessage implements Disc0rdEvent, EventHandler, MessageReceivedHandler {

    private static OnMessage instance = null;
    private static OnMessage getInstance() {
        if (instance == null) {
            instance = new OnMessage();
        }
        return instance;
    }

    @Override
    public void registerDisc0rdEvent() {}

    @Override
    public String getName() {
        return "On Chat Message";
    }

    @Override
    public String getDescription() {
        return "This event is triggered when a certain message is sent. \n\n" +
                "The message is saved in 'event-message', \n" +
                "the message id is saved in 'event-message-id', \n" +
                "the sender name in 'event-sender-name', \n" +
                "the id from the sender in 'event-sender-id' \n" +
                "and the channel id is saved in 'event-channel'.";
    }

    @Override
    public HashMap<String, String> getConfig() {
        HashMap<String, String> config = new HashMap<>();
        config.put("REGEX", "Set a REGEX value here to only listen to certain messages or leave the field empty to listen to all messages.");
        config.put("Channel", "Set a channel id here or leave the field empty to listen to all channels.");
        return config;
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerMessageReceivedEvent(getInstance());
    }

    @Override
    public void handleMessageReceived(MessageReceivedEvent messageReceivedEvent) {
        try {
            if (messageReceivedEvent.getChannel().getType() == ChannelType.TEXT) {
                if (Disc0rd.getJda().getSelfUser().getIdLong() != messageReceivedEvent.getAuthor().getIdLong()) {
                    for (ExtendedEvent extendedEvent:ExtendedEventManager.getInstance().getExtendedEvents()) {
                        if (extendedEvent.getEvent().equals(getName())) {
                            if (extendedEvent.getGuild() == messageReceivedEvent.getGuild().getIdLong()) {
                                ExtendedEventDataStore dataStore = new ExtendedEventDataStore(extendedEvent);
                                dataStore.setDataStore("event-message", messageReceivedEvent.getMessage().getContentRaw());
                                dataStore.setDataStore("event-message-id", messageReceivedEvent.getMessage().getId());
                                dataStore.setDataStore("event-sender-name", messageReceivedEvent.getAuthor().getName());
                                dataStore.setDataStore("event-sender-id", messageReceivedEvent.getAuthor().getId());
                                dataStore.setDataStore("event-channel", messageReceivedEvent.getChannel().getId());
                                if (extendedEvent.getEventConfig().containsKey("Channel")) {
                                    if (!messageReceivedEvent.getChannel().getId().isEmpty()) {
                                        if (extendedEvent.getEventConfig().get("Channel").equals(messageReceivedEvent.getChannel().getId())) {
                                            if (extendedEvent.getEventConfig().containsKey("REGEX")) {
                                                if (messageReceivedEvent.getMessage().getContentRaw().matches(extendedEvent.getEventConfig().get("REGEX"))) {
                                                    ExtendedEventManager.getInstance().executeAction(dataStore);
                                                }
                                            } else {
                                                ExtendedEventManager.getInstance().executeAction(dataStore);
                                            }
                                        }
                                    } else {
                                        if (extendedEvent.getEventConfig().containsKey("REGEX")) {
                                            if (messageReceivedEvent.getMessage().getContentRaw().matches(extendedEvent.getEventConfig().get("REGEX"))) {
                                                ExtendedEventManager.getInstance().executeAction(dataStore);
                                            }
                                        } else {
                                            ExtendedEventManager.getInstance().executeAction(dataStore);
                                        }
                                    }
                                } else {
                                    if (extendedEvent.getEventConfig().containsKey("REGEX")) {
                                        if (messageReceivedEvent.getMessage().getContentRaw().matches(extendedEvent.getEventConfig().get("REGEX"))) {
                                            ExtendedEventManager.getInstance().executeAction(dataStore);
                                        }
                                    } else {
                                        ExtendedEventManager.getInstance().executeAction(dataStore);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
    }
}
