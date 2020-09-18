package com.scolastico.discord_exe.event.handlers;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;

public interface CommandHandler {

    public boolean respondToCommand(String cmd, String[] args, JDA jda, MessageReceivedEvent event, long senderId, long serverId, Member member);

    public HashMap<String, String> getHelpSite(HashMap<String, String> helpSite);

    public HashMap<String, String> getHelpSiteDetails();

    public String getCommandName();

}
