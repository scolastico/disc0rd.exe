package com.scolastico.discord_exe.event.extendedEventSystem.actions;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEventDataStore;
import com.scolastico.discord_exe.event.extendedEventSystem.interfaces.Disc0rdAction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.PermissionException;

import java.util.HashMap;

public class GiveRole implements Disc0rdAction {
    @Override
    public ExtendedEventDataStore doAction(ExtendedEventDataStore dataStore, Integer idFromAction) {
        HashMap<String, String> config = dataStore.getExtendedEvent().getActions().get(idFromAction).getConfig();
        Guild guild = Disc0rd.getJda().getGuildById(dataStore.getExtendedEvent().getGuild());
        if (guild == null) {
            Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[Give Role to User] [" + idFromAction + "] Guild not found!");
            dataStore.setCancelled(true);
            return dataStore;
        }
        Member user = guild.retrieveMemberById(Tools.getInstance().getStringWithVarsFromDataStore(dataStore, config.getOrDefault("User ID", "0"))).complete();
        if (user == null) {
            Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[Give Role to User] [" + idFromAction + "] User not found!");
            dataStore.setCancelled(true);
            return dataStore;
        }
        Role role = guild.getRoleById(Tools.getInstance().getStringWithVarsFromDataStore(dataStore, config.getOrDefault("Role ID", "0")));
        if (role == null) {
            Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[Give Role to User] [" + idFromAction + "] Role not found!");
            dataStore.setCancelled(true);
            return dataStore;
        }
        try {
            guild.addRoleToMember(user, role).complete();
        } catch (PermissionException e) {
            Tools.getInstance().writeGuildLogLine(dataStore.getExtendedEvent().getGuild(), "[Give Role to User] [" + idFromAction + "] Not enough permissions to give role!");
            dataStore.setCancelled(true);
            return dataStore;
        } catch (Exception ignored) {}
        return dataStore;
    }

    @Override
    public String getName() {
        return "Give Role to User";
    }

    @Override
    public String getDescription() {
        return "Gives a role to a user!\n\n" +
                "This action does not save any data.";
    }

    @Override
    public HashMap<String, String> getConfig() {
        HashMap<String, String> ret = new HashMap<>();
        ret.put("User ID", "To get the id mark the user in discord with a \\ in front. E.g .: \\@test");
        ret.put("Role ID", "To get the id mark the role in discord with a \\ in front. E.g .: \\@test");
        return ret;
    }
}
