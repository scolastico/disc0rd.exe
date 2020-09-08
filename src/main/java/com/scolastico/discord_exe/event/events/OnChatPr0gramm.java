package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.Pr0grammAPI;
import com.scolastico.discord_exe.etc.Pr0grammManager;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.event.handlers.MessageReceivedHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnChatPr0gramm implements EventHandler, MessageReceivedHandler {
    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerMessageReceivedEvent(this);
    }

    @Override
    public void handleMessageReceived(MessageReceivedEvent messageReceivedEvent) {
        try {
            if (messageReceivedEvent.getChannel().getType() == ChannelType.TEXT) {
                if (Pr0grammManager.getInstance().isAuthorizedGuild(messageReceivedEvent.getGuild().getIdLong())) {
                    ServerSettings settings = Disc0rd.getMysql().getServerSettings(messageReceivedEvent.getGuild().getIdLong());
                    if (settings.getPr0grammServerConfig().isAutoDetectLinks()) {
                        String message = messageReceivedEvent.getMessage().getContentRaw();
                        Pattern pattern = Pattern.compile("(?:(?:http(?:s?):\\/\\/pr0gramm\\.com)?\\/(?:top|new|user\\/\\w+\\/(?:uploads|likes)|stalk)(?:(?:\\/\\w+)?)\\/)([1-9]\\d*)(?:(?::comment(?:\\d+))?)?", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(message);
                        if (matcher.find()) {
                            long id = Long.parseLong(matcher.group(1));
                            Pr0grammAPI.Pr0grammPost post = Pr0grammManager.getInstance().getPr0grammAPI().getPr0grammPost(id);
                            if (post != null) {
                                EmbedBuilder builder = new EmbedBuilder();
                                builder.setTitle("Pr0gramm.com");
                                builder.setDescription("I found this upload from your message:\n<https://pr0gramm.com/new/" + post.getId() + ">");
                                builder.setColor(new Color(0xEE4D2E));
                                builder.setAuthor(post.getUser(), "https://pr0gramm.com/user/" + post.getUser());
                                builder.setImage(post.getImage());
                                if (post.getFull() != null) builder.setFooter("To see in full resolution: " + post.getFull() + "");
                                messageReceivedEvent.getChannel().sendMessage(builder.build()).queue();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            ErrorHandler.getInstance().handle(ignored);
        }
    }
}
