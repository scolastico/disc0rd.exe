package com.scolastico.discord_exe.etc;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;

public class CommandModule extends ListenerAdapter {

    private ArrayList<CommandHandler> commandHandlers = new ArrayList<CommandHandler>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            String message = event.getMessage().getContentRaw();
            if (message.length() >= 8) {
                if (message.substring(0,8).equalsIgnoreCase("disc0rd/")) {

                    String cmd = message.split(" ")[0];
                    String[] args = message.replaceFirst(cmd, "").split(" ");

                    for (CommandHandler handler:commandHandlers) {

                        try {

                            if (handler.respondToCommand(cmd, args, event.getJDA(), event, event.getAuthor().getIdLong(), event.getChannel().getIdLong())) break;

                        } catch (Exception e) {

                            ErrorHandler.getInstance().handle(e);

                        }

                    }

                }
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
    }

    public void registerCommand(CommandHandler command) {
        if (!commandHandlers.contains(command)) commandHandlers.add(command);
    }

}
