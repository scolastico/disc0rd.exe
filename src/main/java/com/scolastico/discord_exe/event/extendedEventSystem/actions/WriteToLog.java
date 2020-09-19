package com.scolastico.discord_exe.event.extendedEventSystem.actions;

import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventDataStore;
import com.scolastico.discord_exe.event.extendedEventSystem.interfaces.Disc0rdAction;

import java.util.HashMap;

public class WriteToLog implements Disc0rdAction {
    @Override
    public ExtendedEventDataStore doAction(ExtendedEventDataStore dataStore, Integer idFromAction) {
        HashMap<String, String> config = dataStore.getExtendedEvent().getActions().get(idFromAction).getConfig();
        String msg = config.getOrDefault("Message", "");
        if (msg.length() > 100) {
            Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[Write To Bot Log] [" + idFromAction + "] [WARN] Length of message is longer as 100.");
            msg = msg.substring(0,99);
        }
        Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[CUSTOM] [" + idFromAction + "] " + msg);
        return dataStore;
    }

    @Override
    public String getName() {
        return "Write To Bot Log";
    }

    @Override
    public String getDescription() {
        return "Write the defined message to the bot log.";
    }

    @Override
    public HashMap<String, String> getConfig() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("Message", "Write this message to the bot log. Max length is 100!");
        return ret;
    }
}
