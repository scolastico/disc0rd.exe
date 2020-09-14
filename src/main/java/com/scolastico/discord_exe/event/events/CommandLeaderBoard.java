package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.MEE6Api;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.etc.permissions.PermissionsManager;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLeaderBoard implements EventHandler, CommandHandler {

    HashMap<Long, ConfirmData> confirmHashMap = new HashMap<>();

    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId) {
        if (cmd.equalsIgnoreCase("leaderboard")) {
            Member member = event.getGuild().getMember(event.getAuthor());
            if (member == null) return true;
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.red);
            builder.setTitle("Sorry,");
            builder.setDescription("but i think your command is not correct.");
            if (args.length == 0) {
                if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "leaderboard")) {
                    builder.setColor(Color.green);
                    builder.setTitle("You can access the leader board from here:");
                    builder.setDescription(Disc0rd.getConfig().getWebServer().getDomain() + "leaderboard/#" + event.getGuild().getId());
                } else {
                    builder.setDescription("but you dont have the permission to use this command!");
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("enable")) {
                    if (Tools.getInstance().isOwner(event.getGuild(), event.getAuthor())) {
                        ServerSettings settings = Disc0rd.getMysql().getServerSettings(event.getGuild().getIdLong());
                        settings.getLeaderboard().setEnabled(true);
                        Disc0rd.getMysql().setServerSettings(event.getGuild().getIdLong(), settings);
                        builder.setTitle("Success,");
                        builder.setDescription("the leader board is now enabled!");
                        builder.setColor(Color.green);
                    } else {
                        builder.setDescription("but only the guild owner has the permission to use this command!");
                    }
                } else if (args[0].equalsIgnoreCase("disable")) {
                    if (Tools.getInstance().isOwner(event.getGuild(), event.getAuthor())) {
                        ServerSettings settings = Disc0rd.getMysql().getServerSettings(event.getGuild().getIdLong());
                        settings.getLeaderboard().setEnabled(false);
                        Disc0rd.getMysql().setServerSettings(event.getGuild().getIdLong(), settings);
                        builder.setTitle("Success,");
                        builder.setDescription("the leader board is now disabled!");
                        builder.setColor(Color.green);
                    } else {
                        builder.setDescription("but only the guild owner has the permission to use this command!");
                    }
                } else if (args[0].equalsIgnoreCase("reset")) {
                    if (Tools.getInstance().isOwner(event.getGuild(), event.getAuthor())) {
                        clearConfirmData();
                        for (Long timestamp:confirmHashMap.keySet()) {
                            ConfirmData data = confirmHashMap.get(timestamp);
                            if (data.getId() == event.getGuild().getIdLong() && data.getCmd().equals("reset")) {
                                confirmHashMap.remove(timestamp, data);
                                ServerSettings settings = Disc0rd.getMysql().getServerSettings(event.getGuild().getIdLong());
                                settings.setLeaderboard(new ServerSettings.Leaderboard());
                                Disc0rd.getMysql().setServerSettings(event.getGuild().getIdLong(), settings);
                                builder.setColor(Color.YELLOW);
                                builder.setTitle("Success,");
                                builder.setDescription("i resetted the leaderboard database for you!");
                                event.getChannel().sendMessage(builder.build()).queue();
                                return true;
                            }
                        }
                        confirmHashMap.put((System.currentTimeMillis() / 1000L) + 60, new ConfirmData("reset", event.getGuild().getIdLong()));
                        builder.setTitle("WARNING!");
                        builder.setDescription("You are about to reset the entire leaderboard! Please enter `disc0rd/leaderboard reset` in the next 60 seconds again to confirm!");
                    } else {
                        builder.setDescription("but only the guild owner has the permission to use this command!");
                    }
                } else if (args[0].equalsIgnoreCase("banner")) {
                    if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "rank-self")) {
                        return sendBanner(event, member);
                    } else {
                        builder.setDescription("but you dont have the permission to use this command!");
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("banner")) {
                    if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "rank-other")) {
                        try {
                            Pattern pattern = Pattern.compile("(?:<@!)([0-9]+)(?:>)");
                            Matcher matcher = pattern.matcher(args[1]);
                            if (matcher.find()) {
                                String id = matcher.group(1);
                                if (args[1].equalsIgnoreCase("<@!" + id + ">")) {
                                    if (!member.getUser().isBot()) {
                                        return sendBanner(event, member);
                                    }
                                    builder.setDescription("but this user isn't a real user. I only take care of real users!");
                                }
                            }
                        } catch (Exception e) {
                            ErrorHandler.getInstance().handle(e);
                        }
                    } else {
                        builder.setDescription("but you dont have the permission to use this command!");
                    }
                } else if (args[0].equalsIgnoreCase("reset")) {
                    if (Tools.getInstance().isOwner(event.getGuild(), event.getAuthor())) {
                        try {
                            Pattern pattern = Pattern.compile("(?:<@!)([0-9]+)(?:>)");
                            Matcher matcher = pattern.matcher(args[1]);
                            if (matcher.find()) {
                                String id = matcher.group(1);
                                if (args[1].equalsIgnoreCase("<@!" + id + ">")) {
                                    if (!member.getUser().isBot()) {
                                        clearConfirmData();
                                        for (Long timestamp:confirmHashMap.keySet()) {
                                            ConfirmData data = confirmHashMap.get(timestamp);
                                            if (data.getId() == event.getGuild().getIdLong() && data.getCmd().equals("reset-" +  member.getId())) {
                                                confirmHashMap.remove(timestamp, data);
                                                builder.setColor(Color.green);
                                                builder.setTitle("Success,");
                                                builder.setDescription("i deleted the user `" + member.getUser().getAsTag() + "` out of the leaderboard!");
                                                event.getChannel().sendMessage(builder.build()).queue();
                                                return true;
                                            }
                                        }
                                        confirmHashMap.put((System.currentTimeMillis() / 1000L) + 60, new ConfirmData("reset-" +  member.getId(), event.getGuild().getIdLong()));
                                        builder.setTitle("WARNING!");
                                        builder.setDescription("You are about to reset the rank from `" + member.getUser().getAsTag() + "`! Please enter `disc0rd/leaderboard reset " + args[1] + "` in the next 60 seconds again to confirm!");
                                    } else builder.setDescription("but this user isn't a real user. I only take care of real users!");
                                }
                            }
                        } catch (Exception e) {
                            ErrorHandler.getInstance().handle(e);
                        }
                    } else {
                        builder.setDescription("but only the owner has the permission to use this command!");
                    }
                } else if (args[0].equalsIgnoreCase("import")) {
                    if (Tools.getInstance().isOwner(event.getGuild(), event.getAuthor())) {
                        if (args[1].equalsIgnoreCase("mee6")) {
                            clearConfirmData();
                            for (Long timestamp:confirmHashMap.keySet()) {
                                ConfirmData data = confirmHashMap.get(timestamp);
                                if (data.getId() == event.getGuild().getIdLong() && data.getCmd().equals("import-mee6")) {
                                    confirmHashMap.remove(timestamp, data);
                                    ServerSettings settings = Disc0rd.getMysql().getServerSettings(event.getGuild().getIdLong());
                                    settings.setLeaderboard(new ServerSettings.Leaderboard());
                                    HashMap<Long, Long> imported = MEE6Api.getInstance().getXP(event.getGuild().getIdLong());
                                    settings.getLeaderboard().setUsers(imported);
                                    Disc0rd.getMysql().setServerSettings(event.getGuild().getIdLong(), settings);
                                    builder.setColor(Color.YELLOW);
                                    builder.setTitle("Success,");
                                    builder.setDescription("i imported the leaderboard from mee6 for you!");
                                    event.getChannel().sendMessage(builder.build()).queue();
                                    return true;
                                }
                            }
                            confirmHashMap.put((System.currentTimeMillis() / 1000L) + 60, new ConfirmData("import-mee6", event.getGuild().getIdLong()));
                            builder.setTitle("WARNING!");
                            builder.setDescription("This command will reset the entire leaderboard! Please enter `disc0rd/leaderboard import mee6` in the next 60 seconds again to confirm!");
                        } else {
                            builder.setDescription("but this bot isn't supported right now! But you can ask the developers to implement this bot! Send us a mail to `" + Disc0rd.getConfig().getEmail() + "`");
                        }
                    } else {
                        builder.setDescription("but only the owner has the permission to use this command!");
                    }
                }
            }
            event.getChannel().sendMessage(builder.build()).queue();
            return true;
        } else if (cmd.equalsIgnoreCase("rank")) {
            Member member = event.getMember();
            if (member == null) return false;
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.red);
            builder.setTitle("Sorry,");
            builder.setDescription("but i think your command is not correct.");
            if (args.length == 0) {
                if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "rank-self")) {
                    return sendBanner(event, member);
                } else {
                    builder.setDescription("but you dont have the permission to use this command!");
                }
            } else if (args.length == 1) {
                if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "rank-other")) {
                    try {
                        Pattern pattern = Pattern.compile("(?:<@!)([0-9]+)(?:>)");
                        Matcher matcher = pattern.matcher(args[0]);
                        if (matcher.find()) {
                            String id = matcher.group(1);
                            if (args[0].equalsIgnoreCase("<@!" + id + ">")) {
                                if (!member.getUser().isBot()) {
                                    return sendBanner(event, member);
                                }
                                builder.setDescription("but this user isn't a real user. I only take care of real users!");
                            }
                        }
                    } catch (Exception e) {
                        ErrorHandler.getInstance().handle(e);
                    }
                } else {
                    builder.setDescription("but you dont have the permission to use this command!");
                }
            }
            event.getChannel().sendMessage(builder.build()).queue();
            return true;
        }
        return false;
    }

    private boolean sendBanner(MessageReceivedEvent event, Member member) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.red);
        builder.setTitle("Sorry,");
        builder.setDescription("but i think your command is not correct.");
        clearConfirmData();
        for (Long timestamp:confirmHashMap.keySet()) {
            ConfirmData data = confirmHashMap.get(timestamp);
            if (data.getId() == event.getGuild().getIdLong() && data.getCmd().equals("banner-" +  member.getId())) {
                builder.setDescription("but since the rendering of the banner takes up a lot of CPU, I will only render this once per 15 seconds per user!");
                event.getChannel().sendMessage(builder.build()).queue();
                return true;
            }
        }
        confirmHashMap.put((System.currentTimeMillis() / 1000L) + 15, new ConfirmData("banner-" +  member.getId(), event.getGuild().getIdLong()));
        try {
            if (member.getUser().isBot()) return true;
            String svg = Tools.getInstance().getLeaderboardBannerSVG(event.getGuild(), member);
            File file = new File(Disc0rd.getConfig().getTmpDir() + event.getGuild().getId() + "-" + member.getId() + ".png");
            PNGTranscoder coder = new PNGTranscoder();
            StringReader reader = new StringReader(svg);
            TranscoderInput input = new TranscoderInput(reader);
            FileOutputStream outputStream = new FileOutputStream(Disc0rd.getConfig().getTmpDir() + event.getGuild().getId() + "-" + member.getId() + ".png");
            TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);
            try{
                coder.transcode(input, transcoderOutput);
            }finally{
                reader.close();
                outputStream.close();
            }
            if (file.exists()) {
                event.getChannel().sendFile(file, "banner.png").complete();
                if (!file.delete()) {
                    ErrorHandler.getInstance().handle(new Exception("Cant delete file at location '" + file.getPath() + "'!"));
                }
                return true;
            } else {
                ErrorHandler.getInstance().handle(new Exception("Cant find rendered image after rendering in location '" + file.getPath() + "'"));
                builder.setDescription("but i cant render the image. No worries i already reported the error to the developers!");
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
            builder.setDescription("but i cant render the image. No worries i already reported the error to the developers!");
        }
        event.getChannel().sendMessage(builder.build()).queue();
        return true;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        helpSite.put("leaderboard", "Request the leader board url or change settings from the leader board.");
        helpSite.put("rank", "Request your rank banner.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> helpSite = new HashMap<>();
        helpSite.put("leaderboard", "Request the leader board url.");
        helpSite.put("leaderboard banner", "Request your own leader board banner.");
        helpSite.put("leaderboard banner <username>", "Request the leader board banner from an other guild member.");
        helpSite.put("leaderboard import <mee6>", "Import the leader board from an other bot. Like MEE6. Admin Command! Only one use per bot per day!");
        helpSite.put("leaderboard enable", "Enable the leaderboard. Admin Command!");
        helpSite.put("leaderboard disable", "Disable the leaderboard. Admin Command!");
        helpSite.put("leaderboard reset", "Reset the complete leaderboard. Admin Command!");
        helpSite.put("leaderboard reset <user>", "Reset an explicit user. Admin Command!");
        return helpSite;
    }

    @Override
    public String getCommandName() {
        return "leaderboard";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
        PermissionsManager.getInstance().registerPermission("leaderboard", "Allow a user to query the leaderboard url.", true);
        PermissionsManager.getInstance().registerPermission("rank-self", "Allow a user to query their own rank.", true);
        PermissionsManager.getInstance().registerPermission("rank-other", "Allow a user to query the rank of another user.", true);
    }

    private void clearConfirmData() {
        ArrayList<Long> toDelete = new ArrayList<>();
        for (long timestamp:confirmHashMap.keySet()) {
            if ((System.currentTimeMillis() / 1000L) >= timestamp) {
                toDelete.add(timestamp);
            }
        }
        for (long timeStamp:toDelete) {
            confirmHashMap.remove(timeStamp);
        }
    }

    public static class ConfirmData {
        public String cmd;
        public Long id;

        public ConfirmData(String cmd, Long id) {
            this.cmd = cmd;
            this.id = id;
        }

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
