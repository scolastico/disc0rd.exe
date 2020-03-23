package com.scolastico.discord_exe.event;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.event.handlers.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;

public class EventRegister extends ListenerAdapter {

    private ArrayList<CommandHandler> commandHandlers = new ArrayList<CommandHandler>();
    private ArrayList<MessageReceivedHandler> messageReceivedHandlers = new ArrayList<MessageReceivedHandler>();
    private ArrayList<MessageReactionAddHandler> messageReactionAddHandlers = new ArrayList<MessageReactionAddHandler>();
    private ArrayList<MessageReactionRemoveHandler> messageReactionRemoveHandlers = new ArrayList<MessageReactionRemoveHandler>();
    private ArrayList<GuildMemberJoinHandler> guildMemberJoinHandlers = new ArrayList<>();
    private HashMap<ScheduleHandler, Integer> scheduleHandlers = new HashMap<ScheduleHandler, Integer>();
    private static EventRegister instance = null;

    private EventRegister() {}
    public static EventRegister getInstance() {
        if (instance == null) {
            instance = new EventRegister();
        }
        return instance;
    }

    public void registerCommand(CommandHandler handler) {
        if (!commandHandlers.contains(handler)) commandHandlers.add(handler);
    }

    public void registerMessageReceivedEvent(MessageReceivedHandler handler) {
        if (!messageReceivedHandlers.contains(handler)) messageReceivedHandlers.add(handler);
    }

    public void registerSchedule(ScheduleHandler handler) {
        if (!scheduleHandlers.containsKey(handler)) scheduleHandlers.put(handler, 0);
    }

    public void registerMessageReactionAddEvent(MessageReactionAddHandler handler) {
        if (!messageReactionAddHandlers.contains(handler)) messageReactionAddHandlers.add(handler);
    }

    public void registerMessageReactionRemoveEvent(MessageReactionRemoveHandler handler) {
        if (!messageReactionRemoveHandlers.contains(handler)) messageReactionRemoveHandlers.add(handler);
    }

    public void registerGuildMemberJoinEvent(GuildMemberJoinHandler handler) {
        if (!guildMemberJoinHandlers.contains(handler)) guildMemberJoinHandlers.add(handler);
    }

    public void fireSchedule() {
        if (Disc0rd.isReady()) {
            for (ScheduleHandler scheduleHandler:scheduleHandlers.keySet()) {
                try {
                    Integer lastTick = scheduleHandlers.get(scheduleHandler);
                    scheduleHandlers.remove(scheduleHandler);
                    lastTick++;
                    ScheduleHandler.ScheduleTime annotation = scheduleHandler.getClass().getDeclaredAnnotation(ScheduleHandler.ScheduleTime.class);
                    int everyTick = 1;
                    boolean async = true;
                    if (annotation != null) {
                        everyTick = annotation.tick();
                        async = annotation.runAsync();
                    }
                    if (lastTick >= everyTick) {
                        lastTick = 0;
                        if (async) {
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    scheduleHandler.scheduledTask();
                                }
                            });
                            thread.start();
                        } else {
                            scheduleHandler.scheduledTask();
                        }
                    }
                    scheduleHandlers.put(scheduleHandler, lastTick);
                } catch (Exception e) {
                    ErrorHandler.getInstance().handle(e);
                }
            }
        }
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
            if (message.length() >= 8 && message.substring(0,8).equalsIgnoreCase("disc0rd/")) {
                String cmd = message.split(" ")[0];
                String[] args = new ArrayList<String>().toArray(new String[0]);
                if (message.replaceFirst(cmd, "").length() != 0) {
                    args = message.replaceFirst(cmd + " ", "").split(" ");
                }

                for (CommandHandler handler:commandHandlers) {

                    try {

                        if (handler.respondToCommand(cmd, args, event.getJDA(), event, event.getAuthor().getIdLong(), event.getChannel().getIdLong())) {
                            Disc0rd.addExecutedCommand();
                            break;
                        }

                    } catch (Exception e) {

                        ErrorHandler.getInstance().handle(e);

                    }

                }
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        for (MessageReactionAddHandler handler:messageReactionAddHandlers) {
            try {
                handler.onMessageReactionAdd(event);
            } catch (Exception e) {
                ErrorHandler.getInstance().handle(e);
            }
        }
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        for (MessageReactionRemoveHandler handler:messageReactionRemoveHandlers) {
            try {
                handler.onMessageReactionRemove(event);
            } catch (Exception e) {
                ErrorHandler.getInstance().handle(e);
            }
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        for (GuildMemberJoinHandler handler:guildMemberJoinHandlers) {
            try {
                handler.onGuildMemberJoin(event);
            } catch (Exception e) {
                ErrorHandler.getInstance().handle(e);
            }
        }
    }
}
