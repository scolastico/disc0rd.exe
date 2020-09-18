package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.etc.permissions.PermissionsManager;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.CommandHandler;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.HashMap;

public class CommandMoveToMe implements CommandHandler, EventHandler {
    @Override
    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member) {
        if (cmd.equalsIgnoreCase("movetome")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.setTitle("Sorry,");
            builder.setDescription("but you misspelled the command. Try `disc0rd/help moveToMe`");
            if (args.length == 0) {
                if (PermissionsManager.getInstance().checkPermission(event.getGuild(), member, "")) {
                    GuildVoiceState state = member.getVoiceState();
                    if (state != null) {
                        VoiceChannel toMove = state.getChannel();
                        Guild guild = event.getGuild();
                        for (VoiceChannel channel:event.getGuild().getVoiceChannels()) {
                            for (Member members:channel.getMembers()) {
                                guild.moveVoiceMember(members, toMove).queue();
                            }
                        }
                        builder.setColor(Color.GREEN);
                        builder.setTitle("Success,");
                        builder.setDescription("i moved all users to yor channel.");
                    } else {
                        builder.setDescription("but you need to be in a voice channel.");
                    }
                } else {
                    builder.setDescription("but you dont have the permission to use this command.");
                }
            }
            event.getChannel().sendMessage(builder.build()).queue();
            return true;
        }
        return false;
    }

    @Override
    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite) {
        helpSite.put("moveToMe", "Move all online users to you voice chat channel.");
        return helpSite;
    }

    @Override
    public HashMap<String, String> getHelpSiteDetails() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("moveToMe", "Move all online users to you voice chat channel.");
        return ret;
    }

    @Override
    public String getCommandName() {
        return "movetome";
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerCommand(this);
        PermissionsManager.getInstance().registerPermission("move-to-me", "Allow a user to use the moveToMe command.", false);
    }
}
