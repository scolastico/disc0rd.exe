package com.scolastico.discord_exe.event.handlers;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

public interface GuildMemberJoinHandler {

    public void onGuildMemberJoin(GuildMemberJoinEvent event);

}
