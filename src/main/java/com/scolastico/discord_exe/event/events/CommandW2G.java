package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CommandW2G implements EventHandler,CommandHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {
        if (cmd.equalsIgnoreCase("disc0rd/w2g") || cmd.equalsIgnoreCase("disc0rd/watch2gether")) {
            try {
                if (args.length == 0 || args.length == 1) {
                    event.getMessage().delete().queue();
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    String jsonResponse;
                    if (args.length == 0) {
                        jsonResponse = Tools.getInstance().sendPostRequest("https://www.watch2gether.com/rooms/create.json?share=" + URLEncoder.encode(Disc0rd.getConfig().getW2gDefaultPlayback(), StandardCharsets.UTF_8.toString()) + "&api_key=" + Disc0rd.getConfig().getW2gToken());
                    } else {
                        jsonResponse = Tools.getInstance().sendPostRequest("https://www.watch2gether.com/rooms/create.json?share=" + URLEncoder.encode(args[0], StandardCharsets.UTF_8.toString()) + "&api_key=" + Disc0rd.getConfig().getW2gToken());
                    }
                    if (jsonResponse != null) {
                        Object obj = new JSONParser().parse(jsonResponse);
                        if (obj instanceof JSONObject) {
                            Object streamKeyObj = ((JSONObject) obj).get("streamkey");
                            if (streamKeyObj instanceof String) {
                                String streamKey = (String) streamKeyObj;
                                embedBuilder.setColor(Color.green);
                                embedBuilder.setTitle("Watch2Gether");
                                embedBuilder.setDescription("Your Watch2Gether room was created!\nYou can access it with the following url:\n\n<https://watch2gether.com/rooms/" + streamKey + ">");
                                embedBuilder.setThumbnail("https://www.watch2gether.com/static/w2g-oglogo.png");
                                event.getChannel().sendMessage(embedBuilder.build()).queue();
                                return true;
                            }
                        }
                    }
                    embedBuilder.setColor(Color.red);
                    embedBuilder.setTitle("Sorry,");
                    embedBuilder.setDescription("but an unknown error occurred! Please try it again in a few minutes!");
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                    return true;
                }
            } catch (Exception e) {
                ErrorHandler.getInstance().handle(e);
            }
        }
        return false;
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        CommandW2G element = new CommandW2G();
        eventRegister.registerCommand(element);
    }
}
