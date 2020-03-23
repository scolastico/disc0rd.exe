package com.scolastico.discord_exe.event.extendedEventSystem.events;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.event.EventRegister;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEvent;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventDataStore;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventManager;
import com.scolastico.discord_exe.event.extendedEventSystem.interfaces.Disc0rdEvent;
import com.scolastico.discord_exe.event.handlers.EventHandler;
import com.scolastico.discord_exe.event.handlers.GuildMemberLeaveHandler;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;

import java.util.HashMap;

public class OnLeave implements EventHandler, GuildMemberLeaveHandler, Disc0rdEvent {

    @Override
    public void registerDisc0rdEvent() {
        Disc0rd.getEventRegister().registerGuildMemberLeaveEvent(this);
    }

    @Override
    public String getName() {
        return "On Leave";
    }

    @Override
    public String getDescription() {
        return "Triggers if a user leaves your guild!\n\n" +
                "The user name is saved in 'user-name',\n" +
                "the user id is saved in 'user-id',\n" +
                "the user tag is save in 'user-tag'.";
    }

    @Override
    public HashMap<String, String> getConfig() {
        return new HashMap<String, String>();
    }

    @Override
    public void registerEvents(EventRegister eventRegister) {}

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        for (ExtendedEvent extendedEvent : ExtendedEventManager.getInstance().getExtendedEvents(event.getGuild().getIdLong())) {
            if (extendedEvent.getEvent().equals(getName())) {
                ExtendedEventDataStore dataStore = new ExtendedEventDataStore(extendedEvent);
                dataStore.setDataStore("user-name", event.getMember().getUser().getName());
                dataStore.setDataStore("user-id", event.getMember().getId());
                dataStore.setDataStore("user-tag", event.getMember().getUser().getAsTag());
                ExtendedEventManager.getInstance().executeAction(dataStore);
            }
        }
    }
}
