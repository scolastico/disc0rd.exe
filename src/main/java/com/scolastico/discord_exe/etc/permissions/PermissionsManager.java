package com.scolastico.discord_exe.etc.permissions;

import com.scolastico.discord_exe.Disc0rd;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.HashMap;
import java.util.UUID;

public class PermissionsManager {

    HashMap<String, String> permissions = new HashMap<>();
    HashMap<String, Boolean> defaultValues = new HashMap<>();

    private static PermissionsManager instance = null;

    public static PermissionsManager getInstance() {
        if (instance == null) {
            instance = new PermissionsManager();
        }
        return instance;
    }

    private PermissionsManager() {}

    public boolean checkPermission(Guild guild, Member member, String permission) {
        if (guild.getOwnerIdLong() == member.getIdLong()) return true;
        HashMap<UUID, PermissionsData> permissionsData = Disc0rd.getMysql().getServerSettings(guild.getIdLong()).getPermissionsData();
        if (permissionsData.size() == 0) {
            return defaultValues.get(permission);
        }
        for (UUID uuid:permissionsData.keySet()) {
            PermissionsData data = permissionsData.get(uuid);
            if (data.getPermission(permission)) {
                if (data.isUser()) {
                    if (data.getId() == member.getIdLong()) {
                        return true;
                    }
                } else {
                    for (Role role:member.getRoles()) {
                        if (data.getId() == role.getIdLong()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void registerPermission(String permission, String description, boolean defaultValue) {
        if (!permissions.containsKey(permission)) {
            permissions.put(permission, description);
            defaultValues.put(permission, defaultValue);
        }
    }

    public HashMap<String, String> getPermissions() {
        return permissions;
    }

    public HashMap<String, Boolean> getDefaultValues() {
        return defaultValues;
    }

}
