package com.scolastico.discord_exe.event;

import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.MessageReceivedHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EventRegister extends ListenerAdapter {

    private EventRegister() {}
    private static EventRegister instance = null;
    public static EventRegister getInstance() {
        if (instance == null) {
            instance = new EventRegister();
        }
        return instance;
    }

    private ArrayList<CommandHandler> commandHandlers = new ArrayList<CommandHandler>();
    private ArrayList<MessageReceivedHandler> messageReceivedHandlers = new ArrayList<MessageReceivedHandler>();

    public void registerCommand(CommandHandler command) {
        if (!commandHandlers.contains(command)) commandHandlers.add(command);
    }

    public void registerMessageReceivedEvent(MessageReceivedHandler command) {
        if (!messageReceivedHandlers.contains(command)) messageReceivedHandlers.add(command);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        try {

            for (MessageReceivedHandler handler:messageReceivedHandlers) {
                try {
                    handler.handleMessageReceived(event);
                } catch (Exception e) {
                    ErrorHandler.getInstance().handle(e);
                }
            }

            String message = event.getMessage().getContentRaw();
            if (message.length() >= 8) {
                if (message.substring(0,8).equalsIgnoreCase("disc0rd/")) {

                    String cmd = message.split(" ")[0];
                    String[] args = new ArrayList<String>().toArray(new String[0]);
                    if (message.replaceFirst(cmd, "").length() != 0) {
                        args = message.replaceFirst(cmd, "").split(" ");
                    }

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

}
