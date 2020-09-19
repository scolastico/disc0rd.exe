package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.etc.permissions.PermissionsManager;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class CommandW2G implements EventHandler,CommandHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {
        if (cmd.equalsIgnoreCase("w2g") || cmd.equalsIgnoreCase("watch2gether")) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "w2g")) {
                try {
                    if (args.length == 0 || args.length == 1) {
                        event.getMessage().delete().queue();
                        String jsonResponse;
                        if (args.length == 0) {
                            jsonResponse = Tools.getInstance().sendPostRequest(
                                    "https://www.watch2gether.com/rooms/create.json",
                                    "{\"share\":\"" + Disc0rd.getConfig().getW2gDefaultPlayback().replaceAll("(\\\\|\")", "") + "\",\"api_key\":\"" + Disc0rd.getConfig().getW2gToken() + "\",\"bg_color\":\"#23272A\",\"bg_opacity\":\"100\"}"
                            );
                        } else {
                            jsonResponse = Tools.getInstance().sendPostRequest(
                                    "https://www.watch2gether.com/rooms/create.json",
                                    "{\"share\":\"" + args[0].replaceAll("(\\\\|\")", "") + "\",\"api_key\":\"" + Disc0rd.getConfig().getW2gToken() + "\",\"bg_color\":\"#23272A\",\"bg_opacity\":\"100\"}"
                            );
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
            } else {
                embedBuilder.setColor(Color.RED);
                embedBuilder.setTitle("Sorry,");
                embedBuilder.setDescription("but you dont have the permission to use this command!");
                event.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
        return false;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        helpSite.put("w2g", "Open a Watch2Gether room.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> helpSite = new HashMap<>();
        helpSite.put("w2g", "Open a Watch2Gether room.");
        helpSite.put("watch2gether", "Open a Watch2Gether room.");
        helpSite.put("w2g <youtube url>", "Open a Watch2Gether room with a video.");
        helpSite.put("watch2gether <youtube url>", "Open a Watch2Gether room with a video.");
        return helpSite;
    }

    @Override
    public String getCommandName() {
        return "w2g";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
        PermissionsManager.getInstance().registerPermission("w2g", "Allow a user to use the watch 2 gether command.", true);
    }
}
