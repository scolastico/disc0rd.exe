package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.mysql.MysqlHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import com.scolastico.discord_exe.webserver.WebHandler;
import com.sun.net.httpserver.HttpExchange;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.RoleManager;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@WebHandler.WebHandlerRegistration(context = {"/api/v1/color-name/isActive/*", "/api/v1/color-name/change/*"})
public class CommandColorName implements EventHandler, CommandHandler, WebHandler {

    private static HashMap<String, ColorChangeInfo> colorChangeInfos = new HashMap<>();

    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {
        if (cmd.equalsIgnoreCase("disc0rd/color")) {
            if (event.getChannel().getType().equals(ChannelType.TEXT)) {
                event.getMessage().delete().queue();
                EmbedBuilder embedBuilder = new EmbedBuilder();
                MysqlHandler mysql = Disc0rd.getMysql();
                ServerSettings settings = mysql.getServerSettings(event.getGuild().getIdLong());
                ServerSettings.ColorNameConfig colorNameConfig = settings.getColorNameConfig();
                if (args.length == 0) {
                    if (colorNameConfig.isEnabled()) {
                        String random;
                        do {
                            random = Tools.getInstance().getAlphaNumericString(16);
                        } while (colorChangeInfos.containsKey(random));
                        colorChangeInfos.put(random, new ColorChangeInfo(event.getAuthor().getIdLong(), event.getGuild().getIdLong(), random, (new Date().getTime() / 1000) + 300));
                        event.getAuthor().openPrivateChannel().complete().sendMessage("You can change the color of your nickname on the following page: " + Disc0rd.getConfig().getWebServer().getDomain() + "color-name/index.html#" + random).queue();
                        embedBuilder.setTitle("Success,");
                        embedBuilder.setDescription("i send you a private message!");
                        embedBuilder.setColor(Color.green);
                        event.getChannel().sendMessage(embedBuilder.build()).complete().delete().queueAfter(30, TimeUnit.SECONDS);
                        return true;
                    } else {
                        embedBuilder.setTitle("Sorry,");
                        embedBuilder.setDescription("this function is not enabled on this server! Ask the owner if he activates it with `disc0rd/color on`");
                        embedBuilder.setColor(Color.red);
                    }
                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                    return true;
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("activate") || args[0].equalsIgnoreCase("on")) {
                        if (Tools.getInstance().isOwner(event.getGuild(), event.getAuthor())) {
                            colorNameConfig.setEnabled(true);
                            settings.setColorNameConfig(colorNameConfig);
                            mysql.setServerSettings(event.getGuild().getIdLong(), settings);
                            embedBuilder.setTitle("Success,");
                            embedBuilder.setDescription("the setting was successfully saved!");
                            embedBuilder.setColor(Color.green);
                        } else {
                            embedBuilder.setTitle("Sorry,");
                            embedBuilder.setDescription("but only the guild owner has the permission to use this command!");
                            embedBuilder.setColor(Color.red);
                        }
                        event.getChannel().sendMessage(embedBuilder.build()).queue();
                        return true;
                    } else if (args[0].equalsIgnoreCase("deactivate") || args[0].equalsIgnoreCase("off")) {
                        if (Tools.getInstance().isOwner(event.getGuild(), event.getAuthor())) {
                            colorNameConfig.setEnabled(false);
                            settings.setColorNameConfig(colorNameConfig);
                            mysql.setServerSettings(event.getGuild().getIdLong(), settings);
                            embedBuilder.setTitle("Success,");
                            embedBuilder.setDescription("the setting was successfully saved!");
                            embedBuilder.setColor(Color.green);
                        } else {
                            embedBuilder.setTitle("Sorry,");
                            embedBuilder.setDescription("but only the guild owner has the permission to use this command!");
                            embedBuilder.setColor(Color.red);
                        }
                        event.getChannel().sendMessage(embedBuilder.build()).queue();
                        return true;
                    }
                }
            } else {
                event.getChannel().sendMessage("Hey! Please use the server text channels to ask me things like that!").queue();
            }
        }
        return false;
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        EventRegister.getInstance().registerCommand(this);
    }

    @Override
    public String onWebServer(HttpExchange httpExchange) {
        httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
        if (httpExchange.getRequestURI().getPath().startsWith("/api/v1/color-name/isActive/")) {
            removeOldEntry();
            String key = httpExchange.getRequestURI().getPath().replaceFirst("/api/v1/color-name/isActive/", "");
            if (colorChangeInfos.containsKey(key)) {
                return "{\"status\":\"ok\"}";
            } else {
                return "{\"status\":\"error\",\"error\":\"key not valid\"}";
            }
        } else if (httpExchange.getRequestURI().getPath().startsWith("/api/v1/color-name/change/")) {
            removeOldEntry();
            String key = httpExchange.getRequestURI().getPath().replaceFirst("/api/v1/color-name/change/", "");
            if (colorChangeInfos.containsKey(key)) {
                if (httpExchange.getRequestMethod().equals("POST")) {
                    HashMap<String, String> postValues = Tools.getInstance().getPostValuesFromHttpExchange(httpExchange);
                    if (postValues.containsKey("color")) {
                        String colorHash = postValues.get("color");
                        if (colorHash.length() == 6) {
                            Color color = Tools.getInstance().hex2Rgb("#" + colorHash);
                            ColorChangeInfo colorChangeInfo = colorChangeInfos.get(key);
                            Guild guild = Disc0rd.getJda().getGuildById(colorChangeInfo.getGuildId());
                            User user = Disc0rd.getJda().getUserById(colorChangeInfo.getUserId());
                            if (guild != null && user != null) {
                                Member member = guild.getMember(user);
                                if (member != null) {
                                    MysqlHandler mysql = Disc0rd.getMysql();
                                    ServerSettings serverSettings = mysql.getServerSettings(guild.getIdLong());
                                    ServerSettings.ColorNameConfig colorNameConfig = serverSettings.getColorNameConfig();
                                    Role role = null;
                                    if (colorNameConfig.getRoles().containsKey(user.getIdLong())) {
                                        role = guild.getRoleById(colorNameConfig.getRoles().get(user.getIdLong()));
                                    }
                                    if (role == null) {
                                        role = guild.createRole().complete();
                                    }
                                    RoleManager roleManager = role.getManager();
                                    roleManager.setColor(color).queue();
                                    roleManager.setMentionable(false).queue();
                                    roleManager.setName("custom color - " + colorHash).queue();
                                    Member botMember = guild.getMember(Disc0rd.getJda().getSelfUser());
                                    if (botMember == null) {
                                        ErrorHandler.getInstance().handle(new Exception("Not in guild?"));
                                        return "{\"status\":\"error\",\"error\":\"not supported\"}";
                                    }
                                    guild.modifyRolePositions().selectPosition(role).moveTo(botMember.getRoles().get(0).getPositionRaw()-2).queue();
                                    guild.addRoleToMember(member, role).queue();
                                    HashMap<Long, Long> roles = colorNameConfig.getRoles();
                                    roles.put(member.getIdLong(), role.getIdLong());
                                    colorNameConfig.setRoles(roles);
                                    serverSettings.setColorNameConfig(colorNameConfig);
                                    mysql.setServerSettings(guild.getIdLong(), serverSettings);
                                    return "{\"status\":\"ok\"}";
                                }
                            }
                        }
                    }
                }
                colorChangeInfos.remove(key);
                return "{\"status\":\"error\",\"error\":\"not supported\"}";
            } else {
                return "{\"status\":\"error\",\"error\":\"key not valid\"}";
            }
        }
        return "{\"status\":\"error\",\"error\":\"not supported\"}";
    }

    private void removeOldEntry() {
        for (String key:colorChangeInfos.keySet()) {
            ColorChangeInfo colorChangeInfo = colorChangeInfos.get(key);
            if ((new Date().getTime() / 1000) >= colorChangeInfo.getEnabledUntil()) {
                colorChangeInfos.remove(key);
            }
        }
    }

    private static class ColorChangeInfo {
        private long userId;
        private long guildId;
        private String key;
        private long enabledUntil;

        public ColorChangeInfo(long userId, long guildId, String key, long enabledUntil) {
            this.userId = userId;
            this.guildId = guildId;
            this.key = key;
            this.enabledUntil = enabledUntil;
        }

        public long getUserId() {
            return userId;
        }

        public long getGuildId() {
            return guildId;
        }

        public String getKey() {
            return key;
        }

        public long getEnabledUntil() {
            return enabledUntil;
        }
    }

}
