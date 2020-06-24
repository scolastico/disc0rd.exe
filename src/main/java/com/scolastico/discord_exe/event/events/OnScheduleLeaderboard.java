package com.scolastico.discord_exe.event.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.event.handlers.ScheduleHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

@ScheduleHandler.ScheduleTime(tick = 1200, runAsync = true)
public class OnScheduleLeaderboard implements EventHandler, ScheduleHandler {
    @Override
    public void registerEvents(EventRegister eventRegister) {
        eventRegister.registerSchedule(this);
    }

    @Override
    public void scheduledTask() {
        for (Guild guild:Disc0rd.getJda().getGuilds()) {
            ServerSettings settings = Disc0rd.getMysql().getServerSettings(guild.getIdLong());
            for (VoiceChannel channel:guild.getVoiceChannels()) {
                for (Member member:channel.getMembers()) {
                    if (!member.isFake()) if (!member.getUser().isBot()) {
                        GuildVoiceState voiceState = member.getVoiceState();
                        if (voiceState != null) {
                            if (
                                    voiceState.inVoiceChannel() &&
                                            !voiceState.isMuted() &&
                                            !voiceState.isGuildMuted() &&
                                            !voiceState.isDeafened() &&
                                            !voiceState.isGuildDeafened()
                            ) {
                                settings.getLeaderboard().addUserXP(member.getIdLong());
                            }
                        }
                    }
                }
            }
            Disc0rd.getMysql().setServerSettings(guild.getIdLong(), settings);
        }
    }
}
