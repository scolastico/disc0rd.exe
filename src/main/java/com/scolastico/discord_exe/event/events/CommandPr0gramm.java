package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.Pr0grammAPI;
import com.scolastico.discord_exe.etc.Pr0grammManager;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import com.scolastico.discord_exe.webserver.context.Pr0grammOAuth;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;

public class CommandPr0gramm implements EventHandler, CommandHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {
        if (cmd.equalsIgnoreCase("pr0gramm")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.red);
            builder.setTitle("Sorry,");
            builder.setDescription("this didnt work. Check you arguments and try again! For more info try `disc0rd/help pr0gramm`.");
            if (args.length == 0) {
                if (Pr0grammManager.getInstance().isAuthorizedGuild(event.getGuild().getIdLong())) {
                    HashMap<String, String> result = new HashMap<>();
                    for (ServerSettings.Pr0grammServerConfig.Pr0grammSubscription subscription:Pr0grammManager.getInstance().getSubsFromGuild(event.getGuild().getIdLong())) {
                        result.put(subscription.getUsername(), "Channel: <#" + subscription.getChannel() + ">\nSFW: " + (subscription.isSfw() ? "yes" : "no") + "\nNSFW: " + (subscription.isNsfw() ? "yes" : "no") + "\nNSFL: " + (subscription.isNsfl() ? "yes" : "no"));
                    }
                    HashMap<Integer, HashMap<String, String>> pages = Tools.getInstance().splitToSites(result);
                    builder.setColor(Color.green);
                    builder.setTitle("Subscriptions: Page `1` of `" + pages.size() + "`");
                    builder.setDescription("");
                    for (String key:pages.get(1).keySet()) {
                        builder.addField(key, pages.get(1).get(key), true);
                    }
                    builder.setFooter("To see other pages use `disc0rd/pr0gramm <page>`");
                } else {
                    builder.setDescription("this guild isn't authorized! Use `disc0rd/pr0gramm auth` for authorization.");
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("auth")) {
                    if (Tools.getInstance().isOwner(event.getGuild(), event.getAuthor())) {
                        builder.setTitle("Success,");
                        builder.setDescription("i send you a private message!");
                        builder.setColor(Color.green);
                        event.getAuthor().openPrivateChannel().complete().sendMessage("You can login here: <" + Pr0grammManager.getInstance().getConfig().getOAuth().getUrl() + Pr0grammOAuth.getAuthKey(event.getGuild().getIdLong(), event.getAuthor().getIdLong()) + ">").queue();
                    } else {
                        builder.setDescription("only the guild owner has the permission to execute this command.");
                    }
                } else if (args[0].equalsIgnoreCase("deauth")) {
                    if (Tools.getInstance().isOwner(event.getGuild(), event.getAuthor())) {
                        if (Pr0grammManager.getInstance().deAuthGuild(event.getGuild().getIdLong())) {
                            builder.setTitle("Success,");
                            builder.setDescription("the guild has ben de- authorized!");
                            builder.setColor(Color.green);
                        } else {
                            builder.setDescription("this guild isn't authorized!");
                        }
                    } else {
                        builder.setDescription("only the guild owner has the permission to execute this command.");
                    }
                }
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("follow")) {
                    if (Tools.getInstance().isOwner(event.getGuild(), event.getAuthor())) {
                        if (Pr0grammManager.getInstance().isAuthorizedGuild(event.getGuild().getIdLong())) {
                            try {
                                Pr0grammAPI.Pr0grammUser user = Pr0grammManager.getInstance().getPr0grammAPI().getPr0grammUser(args[1]);
                                if (user != null) {
                                    if (Pr0grammManager.getInstance().subscribeUser(event.getGuild().getIdLong(), event.getMessage().getChannel().getIdLong(), user.getName())) {
                                        builder.setTitle("Success,");
                                        builder.setDescription("this user is subscribed to this channel!");
                                        builder.setColor(Color.green);
                                    } else {
                                        builder.setDescription("this subscription exists already!");
                                    }
                                } else {
                                    builder.setDescription("but i cant find this user on Pr0gramm.com.");
                                }
                            } catch (Pr0grammAPI.Pr0grammApiError e) {
                                ErrorHandler.getInstance().handle(e);
                                builder.setDescription("an unknown error occurred pls try it later again.");
                            }
                        } else {
                            builder.setDescription("this guild isn't authorized! Use `disc0rd/pr0gramm auth` for authorization.");
                        }
                    } else {
                        builder.setDescription("but only the guild owner has the permission to execute the command!");
                    }
                } else if (args[0].equalsIgnoreCase("unfollow")) {
                    if (Tools.getInstance().isOwner(event.getGuild(), event.getAuthor())) {
                        if (Pr0grammManager.getInstance().isAuthorizedGuild(event.getGuild().getIdLong())) {
                            if (Pr0grammManager.getInstance().unSubscribeUser(event.getGuild().getIdLong(), event.getMessage().getChannel().getIdLong(), args[1])) {
                                builder.setTitle("Success,");
                                builder.setDescription("this user is un- subscribed from this channel!");
                                builder.setColor(Color.green);
                            } else {
                                builder.setDescription("but i cant see this subscription!");
                            }
                        } else {
                            builder.setDescription("this guild isn't authorized! Use `disc0rd/pr0gramm auth` for authorization.");
                        }
                    } else {
                        builder.setDescription("but only the guild owner has the permission to execute the command!");
                    }
                } else if (args[0].equalsIgnoreCase("toggle") && args[1].equalsIgnoreCase("autoDetectLinks")) {
                    if (Tools.getInstance().isOwner(event.getGuild(), event.getAuthor())) {
                        if (Pr0grammManager.getInstance().isAuthorizedGuild(event.getGuild().getIdLong())) {
                            ServerSettings settings = Disc0rd.getMysql().getServerSettings(event.getGuild().getIdLong());
                            settings.getPr0grammServerConfig().setAutoDetectLinks(!settings.getPr0grammServerConfig().isAutoDetectLinks());
                            Disc0rd.getMysql().setServerSettings(event.getGuild().getIdLong(), settings);
                            builder.setTitle("Success,");
                            builder.setDescription(settings.getPr0grammServerConfig().isAutoDetectLinks() ? "i will now auto detect urls!" : "i will no longer automatically recognize URLs!");
                            builder.setColor(Color.green);
                        } else {
                            builder.setDescription("this guild isn't authorized! Use `disc0rd/pr0gramm auth` for authorization.");
                        }
                    } else {
                        builder.setDescription("but only the guild owner has the permission to execute the command!");
                    }
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("toggle")) {
                    boolean sfw = args[1].equalsIgnoreCase("sfw");
                    boolean nsfw = args[1].equalsIgnoreCase("nsfw");
                    boolean nsfl = args[1].equalsIgnoreCase("nsfl");
                    if (sfw || nsfw || nsfl) {
                        if (Tools.getInstance().isOwner(event.getGuild(), event.getAuthor())) {
                            builder.setDescription("but i cant see this subscription!");
                            String username = null;
                            boolean subSfw = false;
                            boolean subNsfw = false;
                            boolean subNsfl = false;
                            for (ServerSettings.Pr0grammServerConfig.Pr0grammSubscription subscription:Pr0grammManager.getInstance().getSubsFromGuild(event.getGuild().getIdLong())) {
                                if (subscription.getChannel() == event.getChannel().getIdLong() && subscription.getUsername().equalsIgnoreCase(args[2])) {
                                    username = subscription.getUsername();
                                    subSfw = subscription.isSfw();
                                    subNsfw = subscription.isNsfw();
                                    subNsfl = subscription.isNsfl();
                                }
                            }
                            if (username != null) {
                                if (sfw) {
                                    Pr0grammManager.getInstance().setFollowedChannels(event.getGuild().getIdLong(), event.getChannel().getIdLong(), username, !subSfw, subNsfw, subNsfl);
                                } else if (nsfw) {
                                    Pr0grammManager.getInstance().setFollowedChannels(event.getGuild().getIdLong(), event.getChannel().getIdLong(), username, subSfw, !subNsfw, subNsfl);
                                } else {
                                    Pr0grammManager.getInstance().setFollowedChannels(event.getGuild().getIdLong(), event.getChannel().getIdLong(), username, subSfw, subNsfw, !subNsfl);
                                }
                                builder.setTitle("Success,");
                                builder.setDescription("the followed channel was toggled!");
                                builder.setColor(Color.green);
                            }
                        } else {
                            builder.setDescription("but only the guild owner has the permission to execute the command!");
                        }
                    }
                }
            }
            event.getChannel().sendMessage(builder.build()).queue();
        }
        return false;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> details = new HashMap<>();
        details.put("pr0gramm auth", "Authenticate this guild with your Pr0gramm account! Admin Command!");
        details.put("pr0gramm deauth", "De- Authenticate this guild with your Pr0gramm account! Admin Command! Attention, this also deletes all follows!");
        details.put("pr0gramm follow <user>", "Follow a user in this channel. Admin Command!");
        details.put("pr0gramm unfollow <user>", "Un- Follow a user in this channel. Admin Command!");
        details.put("pr0gramm toggle <sfw/nsfw/nsfl> <user>", "Set the followed channels from a user! Admin Command!");
        details.put("pr0gramm toggle autoDetectLinks", "Sets whether urls should be recognized automatically! Admin Command!");
        details.put("pr0gramm", "Lists all follows!");
        return details;
    }

    @Override
    public String getCommandName() {
        return "pr0gramm";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
    }
}
