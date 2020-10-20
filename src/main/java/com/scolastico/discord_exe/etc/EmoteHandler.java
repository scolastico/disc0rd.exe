package com.scolastico.discord_exe.etc;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.config.ConfigDataStore;
import net.dv8tion.jda.api.entities.Emote;

public class EmoteHandler {

    private final Emote emoteOk;
    private final Emote emoteNo;
    private final Emote emoteNoPermission;
    private final Emote emotePlay;
    private final Emote emotePause;

    private static EmoteHandler instance = null;

    public static EmoteHandler getInstance() {
        if (instance == null) {
            instance = new EmoteHandler();
        }
        return instance;
    }

    private EmoteHandler() {
        ConfigDataStore config = Disc0rd.getConfig();
        emoteOk = Disc0rd.getJda().getEmoteById(config.getEmotes().getEmoteOk());
        if (emoteOk == null) {
            ErrorHandler.getInstance().handleFatal(new Exception("Cant find emote 'ok'"));
        }
        emoteNo = Disc0rd.getJda().getEmoteById(config.getEmotes().getEmoteNo());
        if (emoteNo == null) {
            ErrorHandler.getInstance().handleFatal(new Exception("Cant find emote 'no'"));
        }
        emoteNoPermission = Disc0rd.getJda().getEmoteById(config.getEmotes().getEmoteNoPermission());
        if (emoteNoPermission == null) {
            ErrorHandler.getInstance().handleFatal(new Exception("Cant find emote 'no permission'"));
        }
        emotePause = Disc0rd.getJda().getEmoteById(config.getEmotes().getEmotePause());
        if (emotePause == null) {
            ErrorHandler.getInstance().handleFatal(new Exception("Cant find emote 'pause'"));
        }
        emotePlay = Disc0rd.getJda().getEmoteById(config.getEmotes().getEmotePlay());
        if (emotePlay == null) {
            ErrorHandler.getInstance().handleFatal(new Exception("Cant find emote 'play'"));
        }
    }

    public Emote getEmoteOk() {
        return emoteOk;
    }

    public Emote getEmoteNo() {
        return emoteNo;
    }

    public Emote getEmoteNoPermission() {
        return emoteNoPermission;
    }

    public Emote getEmotePlay() {
        return emotePlay;
    }

    public Emote getEmotePause() {
        return emotePause;
    }

}
