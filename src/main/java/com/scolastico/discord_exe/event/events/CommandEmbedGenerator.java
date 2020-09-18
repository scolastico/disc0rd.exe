package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.ScheduleTask;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.etc.permissions.PermissionsManager;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.event.handlers.MessageReactionAddHandler;
import com.scolastico.discord_exe.event.handlers.MessageReceivedHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandEmbedGenerator implements CommandHandler, MessageReceivedHandler, MessageReactionAddHandler, EventHandler {
    ArrayList<EmbedGeneratorData> data = new ArrayList<>();

    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {
        if (cmd.equalsIgnoreCase("embedGenerator")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.setTitle("Sorry,");
            builder.setDescription("but i cant find this command. Check your arguments or try `disc0rd/help embedGenerator`.");
            if (args.length == 0) {
                if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "embed-builder")) {
                    event.getMessage().delete().queue();
                    clearData();
                    for (EmbedGeneratorData embedGeneratorData:data) {
                        if (embedGeneratorData.getUserId() == member.getIdLong()) {
                            builder.setDescription("but you have already a generator open. Please finish or close it before you open another one.");
                            event.getChannel().sendMessage(builder.build()).queue();
                            return true;
                        }
                    }
                    builder.setColor(Color.GREEN);
                    builder.setTitle("Embed Generator");
                    builder.setDescription(
                            "Ok please enter the now the title for your embed message.\n" +
                            "Or react with ...\n" +
                            " ... :x: for canceling the generator.\n" +
                            " ... :hash: for an empty title."
                    );
                    builder.setFooter("Please react in 60 seconds or i will close the generator.");
                    Message message = event.getChannel().sendMessage(builder.build()).complete();
                    message.addReaction("❌").queue();
                    message.addReaction("#️⃣").queue();
                    data.add(new EmbedGeneratorData(member.getIdLong(), event.getChannel(), event.getGuild().getIdLong(), message));
                    return true;
                } else {
                    builder.setDescription("but you dont have the permission to use this command!");
                }
            }
            event.getChannel().sendMessage(builder.build()).queue();
            return true;
        }
        return false;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        helpSite.put("embedGenerator", "Start the embed generator to create a embed message.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("embedGenerator", "Start the embed generator to create a embed message.");
        return ret;
    }

    @Override
    public String getCommandName() {
        return "embedGenerator";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
        eventRegister.registerMessageReactionAddEvent(this);
        eventRegister.registerMessageReceivedEvent(this);
        PermissionsManager.getInstance().registerPermission("embed-builder", "Allow a user to use the embed generator command.", false);
        ScheduleTask.getInstance().runScheduledTaskRepeat(() -> {
            try {
                clearData();
            } catch (Exception e) {
                ErrorHandler.getInstance().handle(e);
            }
        },20, 20, true);
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        ArrayList<EmbedGeneratorData> toDelete = new ArrayList<>();
        clearData();
        for (EmbedGeneratorData embedGeneratorData:data) {
            if (embedGeneratorData.getLastMessage().getIdLong() == event.getMessageIdLong()) {
                if (embedGeneratorData.getUserId() == event.getUserIdLong()) {
                    EmbedBuilder builder = new EmbedBuilder();
                    if (event.getReactionEmote().getName().equalsIgnoreCase("❌")) {
                        embedGeneratorData.getLastMessage().delete().queue();
                        builder.setColor(Color.RED);
                        builder.setTitle("Closed!");
                        builder.setDescription("I cancelled the embed generation.");
                        toDelete.add(embedGeneratorData);
                    } else if (event.getReactionEmote().getName().equalsIgnoreCase("#️⃣")) {
                        builder.setColor(Color.GREEN);
                        builder.setTitle("Embed Generator");
                        if (embedGeneratorData.getStep() == 0) {
                            embedGeneratorData.setStep(1);
                            embedGeneratorData.getLastMessage().delete().queue();
                            builder.setDescription(
                                    "Ok please enter the now the description for your embed message.\n" +
                                    "Or react with ...\n" +
                                    " ... :x: for canceling the generator.\n" +
                                    " ... :hash: for an empty description."
                            );
                            builder.setFooter("Please react in 60 seconds or i will close the generator.");
                            Message message = event.getChannel().sendMessage(builder.build()).complete();
                            message.addReaction("❌").queue();
                            message.addReaction("#️⃣").queue();
                            embedGeneratorData.setLastMessage(message);
                            embedGeneratorData.setLastUnixTime(Tools.getInstance().getUnixTimeStamp());
                        } else if (embedGeneratorData.getStep() == 1) {
                            embedGeneratorData.setStep(2);
                            embedGeneratorData.getLastMessage().delete().queue();
                            builder.setDescription(
                                    "Ok please enter the now an hex color value.\n" +
                                    "You can get one from here: <https://www.google.com/search?q=hex+color+picker>\n" +
                                    "Or react with ...\n" +
                                    " ... :x: for canceling the generator.\n" +
                                    " ... :hash: for no color."
                            );
                            builder.setFooter("Please react in 60 seconds or i will close the generator.");
                            Message message = event.getChannel().sendMessage(builder.build()).complete();
                            message.addReaction("❌").queue();
                            message.addReaction("#️⃣").queue();
                            embedGeneratorData.setLastMessage(message);
                            embedGeneratorData.setLastUnixTime(Tools.getInstance().getUnixTimeStamp());
                        } else if (embedGeneratorData.getStep() == 2) {
                            embedGeneratorData.getLastMessage().delete().queue();
                            toDelete.add(embedGeneratorData);
                            if (embedGeneratorData.getTitle() == null && embedGeneratorData.getDescription() == null) {
                                builder.setColor(Color.RED);
                                builder.setTitle("Sorry,");
                                builder.setDescription("but i cant create your message because the title and the description is empty!");
                            } else {
                                builder.setColor(embedGeneratorData.getColor());
                                builder.setTitle(embedGeneratorData.getTitle());
                                builder.setDescription(embedGeneratorData.getDescription());
                            }
                            event.getChannel().sendMessage(builder.build()).queue();
                        }
                        continue;
                    }
                    event.getChannel().sendMessage(builder.build()).queue();
                }
            }
        }
        for (EmbedGeneratorData delete:toDelete) {
            data.remove(delete);
        }
    }

    @Override
    public void handleMessageReceived(MessageReceivedEvent event) {
        ArrayList<EmbedGeneratorData> toDelete = new ArrayList<>();
        clearData();
        for (EmbedGeneratorData embedGeneratorData:data) {
            if (embedGeneratorData.getChannel().getIdLong() == event.getChannel().getIdLong()) {
                if (embedGeneratorData.getUserId() == event.getAuthor().getIdLong()) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.GREEN);
                    builder.setTitle("Embed Generator");
                    if (embedGeneratorData.getStep() == 0) {
                        embedGeneratorData.setTitle(event.getMessage().getContentRaw());
                        event.getMessage().delete().queue();
                        embedGeneratorData.setStep(1);
                        embedGeneratorData.getLastMessage().delete().queue();
                        builder.setDescription(
                                "Ok please enter the now the description for your embed message.\n" +
                                        "Or react with ...\n" +
                                        " ... :x: for canceling the generator.\n" +
                                        " ... :hash: for an empty description."
                        );
                        builder.setFooter("Please react in 60 seconds or i will close the generator.");
                        Message message = event.getChannel().sendMessage(builder.build()).complete();
                        message.addReaction("❌").queue();
                        message.addReaction("#️⃣").queue();
                        embedGeneratorData.setLastMessage(message);
                        embedGeneratorData.setLastUnixTime(Tools.getInstance().getUnixTimeStamp());
                    } else if (embedGeneratorData.getStep() == 1) {
                        embedGeneratorData.setDescription(event.getMessage().getContentRaw());
                        event.getMessage().delete().queue();
                        embedGeneratorData.setStep(2);
                        embedGeneratorData.getLastMessage().delete().queue();
                        builder.setDescription(
                                "Ok please enter the now an hex color value.\n" +
                                        "You can get one from here: <https://www.google.com/search?q=hex+color+picker>\n" +
                                        "Or react with ...\n" +
                                        " ... :x: for canceling the generator.\n" +
                                        " ... :hash: for no color."
                        );
                        builder.setFooter("Please react in 60 seconds or i will close the generator.");
                        Message message = event.getChannel().sendMessage(builder.build()).complete();
                        message.addReaction("❌").queue();
                        message.addReaction("#️⃣").queue();
                        embedGeneratorData.setLastMessage(message);
                        embedGeneratorData.setLastUnixTime(Tools.getInstance().getUnixTimeStamp());
                    } else if (embedGeneratorData.getStep() == 2) {
                        try {
                            String tmp = event.getMessage().getContentRaw();
                            event.getMessage().delete().queue();
                            if (!tmp.startsWith("#")) {
                                tmp = "#" + tmp;
                            }
                            embedGeneratorData.getLastMessage().delete().queue();
                            toDelete.add(embedGeneratorData);
                            if (embedGeneratorData.getTitle() == null && embedGeneratorData.getDescription() == null) {
                                builder.setColor(Color.RED);
                                builder.setTitle("Sorry,");
                                builder.setDescription("but i cant create your message because the title and the description is empty!");
                            } else {
                                builder.setColor(Tools.getInstance().hex2Rgb(tmp));
                                builder.setTitle(embedGeneratorData.getTitle());
                                builder.setDescription(embedGeneratorData.getDescription());
                            }
                        } catch (Exception ignored) {
                            builder.setColor(Color.RED);
                            builder.setTitle("Sorry,");
                            builder.setDescription("but i cant set this color. Is your color a valid hex string?");
                        }
                        event.getChannel().sendMessage(builder.build()).queue();
                    }
                }
            }
        }
        for (EmbedGeneratorData delete:toDelete) {
            data.remove(delete);
        }
    }

    private void clearData() {
        ArrayList<EmbedGeneratorData> toDelete = new ArrayList<>();
        for (EmbedGeneratorData data:this.data) {
            if (data.getLastUnixTime() < Tools.getInstance().getUnixTimeStamp()-60) {
                toDelete.add(data);
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setTitle("Sorry,");
        builder.setDescription("but i closed the generator because you have not responded since 60 seconds!");
        for (EmbedGeneratorData delete:toDelete) {
            delete.getLastMessage().delete().queue();
            delete.getChannel().sendMessage(builder.build()).queue();
            this.data.remove(delete);
        }
    }

    private static class EmbedGeneratorData {
        private int step = 0;
        private String title = null;
        private String description = null;
        private Color color = null;
        private final long userId;
        private final MessageChannel channel;
        private final long guildId;
        private Message lastMessage;
        private long lastUnixTime;

        public EmbedGeneratorData(long userId, MessageChannel channel, long guildId, Message lastMessage) {
            this.userId = userId;
            this.channel = channel;
            this.guildId = guildId;
            this.lastMessage = lastMessage;
            this.lastUnixTime = Tools.getInstance().getUnixTimeStamp();
        }

        public long getLastUnixTime() {
            return lastUnixTime;
        }

        public void setLastUnixTime(long lastUnixTime) {
            this.lastUnixTime = lastUnixTime;
        }

        public Message getLastMessage() {
            return lastMessage;
        }

        public void setLastMessage(Message lastMessage) {
            this.lastMessage = lastMessage;
        }

        public long getUserId() {
            return userId;
        }

        public MessageChannel getChannel() {
            return channel;
        }

        public long getGuildId() {
            return guildId;
        }

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }
    }
}
