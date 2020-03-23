package com.scolastico.discord_exe.event.handlers;

import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;

public interface GuildMemberLeaveHandler {

    public void onGuildMemberLeave(GuildMemberLeaveEvent event);

}
