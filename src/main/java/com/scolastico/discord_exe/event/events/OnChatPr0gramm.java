package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
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
                        Pattern pattern = Pattern.compile("(https://pr0gramm.com/(new|top)/[0-9]+)", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(message);
                        if (matcher.find()) {
                            String url = matcher.group(1);
                            long id = Long.parseLong(url.substring(25));
                            Pr0grammAPI.Pr0grammGetItemsRequestGenerator generator = Pr0grammManager.getInstance().getPr0grammAPI().generateGetItemsRequestGenerator();
                            Pr0grammAPI.Pr0grammFlagCalculator calculator = new Pr0grammAPI.Pr0grammFlagCalculator();
                            calculator.setSfw(true);
                            calculator.setNsfw(true);
                            calculator.setNsfl(true);
                            generator.setFlagCalculator(calculator);
                            generator.setNewer(id-1);
                            Pr0grammAPI.Pr0grammPost[] post = Pr0grammManager.getInstance().getPr0grammAPI().getPr0grammPosts(generator);
                            if (post.length >= 1) {
                                if (post[0].getId() == id) {
                                    EmbedBuilder builder = new EmbedBuilder();
                                    builder.setTitle("Pr0gramm.com");
                                    builder.setDescription("I found this upload from your message:\n<https://pr0gramm.com/new/" + post[0].getId() + ">");
                                    builder.setColor(new Color(0xEE4D2E));
                                    builder.setAuthor(post[0].getUser(), "https://pr0gramm.com/user/" + post[0].getUser());
                                    builder.setImage(post[0].getImage());
                                    if (post[0].getFull() != null) builder.setFooter("To see in full resolution: " + post[0].getFull() + "");
                                    messageReceivedEvent.getChannel().sendMessage(builder.build()).queue();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
