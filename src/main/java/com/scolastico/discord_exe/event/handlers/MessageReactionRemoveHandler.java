package com.scolastico.discord_exe.event.handlers;

import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

public interface MessageReactionRemoveHandler {

    public void onMessageReactionRemove(MessageReactionRemoveEvent event);

}
