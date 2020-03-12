package com.scolastico.discord_exe.event.handlers;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public interface MessageReactionAddHandler {

    public void onMessageReactionAdd(MessageReactionAddEvent event);

}
