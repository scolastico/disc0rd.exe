package com.scolastico.discord_exe.event.handlers;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface MessageReceivedHandler {

    public void handleMessageReceived(MessageReceivedEvent messageReceivedEvent);

}
