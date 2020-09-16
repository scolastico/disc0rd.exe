package com.scolastico.discord_exe.etc.permissions;

import java.util.HashMap;

public class PermissionsData {

    private boolean isUser = false;
    private HashMap<String, Boolean> permissions = new HashMap<>();
    private long id;

    public PermissionsData(long id) {
        this.id = id;
    }

    public boolean getPermission(String permission) {
        if (permissions.containsKey(permission)) {
            return permissions.get(permission);
        }
        return false;
    }

    public void setPermission(String permission, boolean bool) {
        permissions.remove(permission);
        permissions.put(permission, bool);
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    public HashMap<String, Boolean> getPermissions() {
        return permissions;
    }

    public void setPermissions(HashMap<String, Boolean> permissions) {
        this.permissions = permissions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
