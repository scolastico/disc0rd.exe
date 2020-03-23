package com.scolastico.discord_exe.event.extendedEventSystem.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEvent;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventDataStore;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventManager;
import com.scolastico.discord_exe.event.extendedEventSystem.interfaces.Disc0rdEvent;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.event.handlers.GuildMemberJoinHandler;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

import java.util.HashMap;

public class OnJoin implements EventHandler, GuildMemberJoinHandler, Disc0rdEvent {
    @Override
    public void registerDisc0rdEvent() {
        Disc0rd.getEventRegister().registerGuildMemberJoinEvent(this);
    }

    @Override
    public String getName() {
        return "On Join";
    }

    @Override
    public String getDescription() {
        return "Triggers if a user joins your guild!\n\n" +
                "The user name is saved in 'user-name', \n" +
                "the user id is saved in 'user-id'.";
    }

    @Override
    public HashMap<String, String> getConfig() {
        HashMap<String, String> config = new HashMap<>();
        return config;
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {}

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        for (ExtendedEvent extendedEvent : ExtendedEventManager.getInstance().getExtendedEvents(event.getGuild().getIdLong())) {
            if (extendedEvent.getEvent().equals(getName())) {
                ExtendedEventDataStore dataStore = new ExtendedEventDataStore(extendedEvent);
                dataStore.setDataStore("user-name", event.getMember().getUser().getName());
                dataStore.setDataStore("user-id", event.getMember().getId());
                ExtendedEventManager.getInstance().executeAction(dataStore);
            }
        }
    }
}
