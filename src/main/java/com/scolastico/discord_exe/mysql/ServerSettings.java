package com.scolastico.discord_exe.mysql;

import com.scolastico.discord_exe.event.extendedEventSystem.ExtendedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ServerSettings {

    private boolean isSpecialServer = false;
    private HashMap<String, String> shortCuts = new HashMap<String, String>();
    private ColorNameConfig colorNameConfig = new ColorNameConfig();
    private ServerLimits serverLimits = new ServerLimits();
    private String log = "[LOG BEGINNING]";
    private ArrayList<ExtendedEvent> extendedEvents = new ArrayList<>();
    private String cmdPrefix = "$";
    private Pr0grammServerConfig pr0grammServerConfig = new Pr0grammServerConfig();

    public Pr0grammServerConfig getPr0grammServerConfig() {
        return pr0grammServerConfig;
    }

    public void setPr0grammServerConfig(Pr0grammServerConfig pr0grammServerConfig) {
        this.pr0grammServerConfig = pr0grammServerConfig;
    }

    public String getCmdPrefix() {
        return cmdPrefix;
    }

    public void setCmdPrefix(String cmdPrefix) {
        this.cmdPrefix = cmdPrefix;
    }

    public ArrayList<ExtendedEvent> getExtendedEvents() {
        return extendedEvents;
    }

    public void setExtendedEvents(ArrayList<ExtendedEvent> extendedEvents) {
        this.extendedEvents = extendedEvents;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public ServerLimits getServerLimits() {
        return serverLimits;
    }

    public void setServerLimits(ServerLimits serverLimits) {
        this.serverLimits = serverLimits;
    }

    public ColorNameConfig getColorNameConfig() {
        return colorNameConfig;
    }

    public void setColorNameConfig(ColorNameConfig colorNameConfig) {
        this.colorNameConfig = colorNameConfig;
    }

    public boolean isSpecialServer() {
        return isSpecialServer;
    }

    public void setSpecialServer(boolean specialServer) {
        isSpecialServer = specialServer;
    }

    public HashMap<String, String> getShortCuts() {
        return shortCuts;
    }

    public void setShortCuts(HashMap<String, String> shortCuts) {
        this.shortCuts = shortCuts;
    }

    public static class Pr0grammServerConfig {

        private String linkedAccount = null;
        private long linkedMember = 0;
        private ArrayList<Pr0grammSubscription> subscriptions = new ArrayList<>();
        private boolean autoDetectLinks = true;

        public boolean isAutoDetectLinks() {
            return autoDetectLinks;
        }

        public void setAutoDetectLinks(boolean autoDetectLinks) {
            this.autoDetectLinks = autoDetectLinks;
        }

        public long getLinkedMember() {
            return linkedMember;
        }

        public void setLinkedMember(long linkedMember) {
            this.linkedMember = linkedMember;
        }

        public String getLinkedAccount() {
            return linkedAccount;
        }

        public void setLinkedAccount(String linkedAccount) {
            this.linkedAccount = linkedAccount;
        }

        public ArrayList<Pr0grammSubscription> getSubscriptions() {
            return subscriptions;
        }

        public void setSubscriptions(ArrayList<Pr0grammSubscription> subscriptions) {
            this.subscriptions = subscriptions;
        }

        public void addSubscription(Pr0grammSubscription subscription) {
            if (!subscriptions.contains(subscription)) subscriptions.add(subscription);
        }

        public void removeSubscription(Pr0grammSubscription subscription) {
            subscriptions.remove(subscription);
        }

        public static class Pr0grammSubscription {
            private String username;
            private boolean sfw;
            private boolean nsfw;
            private boolean nsfl;
            private long channel;

            public Pr0grammSubscription(String username, boolean sfw, boolean nsfw, boolean nsfl, long channel) {
                this.username = username;
                this.sfw = sfw;
                this.nsfw = nsfw;
                this.nsfl = nsfl;
                this.channel = channel;
            }

            public long getChannel() {
                return channel;
            }

            public void setChannel(long channel) {
                this.channel = channel;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public boolean isSfw() {
                return sfw;
            }

            public void setSfw(boolean sfw) {
                this.sfw = sfw;
            }

            public boolean isNsfw() {
                return nsfw;
            }

            public void setNsfw(boolean nsfw) {
                this.nsfw = nsfw;
            }

            public boolean isNsfl() {
                return nsfl;
            }

            public void setNsfl(boolean nsfl) {
                this.nsfl = nsfl;
            }
        }

    }

    public static class ColorNameConfig {

        private boolean isEnabled = false;
        private int sensitivity = 30;
        private List<String> disabledColors = Arrays.asList("#ffffff", "#2C2F33", "#23272A");
        private HashMap<Long, Long> roles = new HashMap<>();

        public List<String> getDisabledColors() {
            return disabledColors;
        }

        public void setDisabledColors(List<String> disabledColors) {
            this.disabledColors = disabledColors;
        }

        public int getSensitivity() {
            return sensitivity;
        }

        public void setSensitivity(int sensitivity) {
            this.sensitivity = sensitivity;
        }

        public boolean isEnabled() {
            return isEnabled;
        }

        public void setEnabled(boolean enabled) {
            isEnabled = enabled;
        }

        public HashMap<Long, Long> getRoles() {
            return roles;
        }

        public void setRoles(HashMap<Long, Long> roles) {
            this.roles = roles;
        }

    }

    public static class ServerLimits {
        private int logLines = 0;
        private int events = 0;
        private int actionsPerEvent = 0;

        public int getLogLines() {
            return logLines;
        }

        public void setLogLines(int logLines) {
            this.logLines = logLines;
        }

        public int getEvents() {
            return events;
        }

        public void setEvents(int events) {
            this.events = events;
        }

        public int getActionsPerEvent() {
            return actionsPerEvent;
        }

        public void setActionsPerEvent(int actionsPerEvent) {
            this.actionsPerEvent = actionsPerEvent;
        }
    }

}
