package com.scolastico.discord_exe.config;

public class ConfigDataStore {

  private String discordToken = "token";
  private WebServerData webServer = new WebServerData();
  private MysqlData mysql = new MysqlData();
  private int maxErrorCountToShutDown = 10;
  private String w2gToken = "token";
  private String w2gDefaultPlayback = "https://youtu.be/J5SRm9Wk-fM";
  private OwnerPanelData ownerPanel = new OwnerPanelData();
  private AdminPanelData adminPanel = new AdminPanelData();
  private DefaultLimits defaultLimits = new DefaultLimits();
  private String tmpDir = "tmp/";
  private String email = "support@disc0rd.me";
  private Integer musicPlayerTimout = 300;
  private Integer garbageCollectorTime = 900;
  private Spotify spotify = new Spotify();
  private String discordInvite = "https://discord.gg/BCQmpCy";
  private String documentation = "http://go.scolasti.co/disc0rddocumentation";
  private Emotes emotes = new Emotes();
  private String motd = "Try disc0rd/help!";
  private String twitchUrl = "";
  private Statistics statistics = new Statistics();
  private int mysqlCache = 30;

  public int getMysqlCache() {
    return mysqlCache;
  }

  public void setMysqlCache(int mysqlCache) {
    this.mysqlCache = mysqlCache;
  }

  public Statistics getStatistics() {
    return statistics;
  }

  public void setStatistics(Statistics statistics) {
    this.statistics = statistics;
  }

  public String getTwitchUrl() {
    return twitchUrl;
  }

  public void setTwitchUrl(String twitchUrl) {
    this.twitchUrl = twitchUrl;
  }

  public String getMotd() {
    return motd;
  }

  public void setMotd(String motd) {
    this.motd = motd;
  }

  public Emotes getEmotes() { return emotes; }

  public void setEmotes(Emotes emotes) { this.emotes = emotes; }

  public String getDocumentation() { return documentation; }

  public void setDocumentation(String documentation) {
    this.documentation = documentation;
  }

  public String getDiscordInvite() { return discordInvite; }

  public void setDiscordInvite(String discordInvite) {
    this.discordInvite = discordInvite;
  }

  public Spotify getSpotify() { return spotify; }

  public void setSpotify(Spotify spotify) { this.spotify = spotify; }

  public Integer getGarbageCollectorTime() { return garbageCollectorTime; }

  public void setGarbageCollectorTime(Integer garbageCollectorTime) {
    this.garbageCollectorTime = garbageCollectorTime;
  }

  public Integer getMusicPlayerTimout() { return musicPlayerTimout; }

  public void setMusicPlayerTimout(Integer musicPlayerTimout) {
    this.musicPlayerTimout = musicPlayerTimout;
  }

  public String getEmail() { return email; }

  public void setEmail(String email) { this.email = email; }

  public String getTmpDir() { return tmpDir; }

  public void setTmpDir(String tmpDir) { this.tmpDir = tmpDir; }

  public AdminPanelData getAdminPanel() { return adminPanel; }

  public void setAdminPanel(AdminPanelData adminPanel) {
    this.adminPanel = adminPanel;
  }

  public DefaultLimits getDefaultLimits() { return defaultLimits; }

  public void setDefaultLimits(DefaultLimits defaultLimits) {
    this.defaultLimits = defaultLimits;
  }

  public OwnerPanelData getOwnerPanel() { return ownerPanel; }

  public void setOwnerPanel(OwnerPanelData ownerPanel) {
    this.ownerPanel = ownerPanel;
  }

  public String getW2gDefaultPlayback() { return w2gDefaultPlayback; }

  public void setW2gDefaultPlayback(String w2gDefaultPlayback) {
    this.w2gDefaultPlayback = w2gDefaultPlayback;
  }

  public String getW2gToken() { return w2gToken; }

  public void setW2gToken(String w2gToken) { this.w2gToken = w2gToken; }

  public int getMaxErrorCountToShutDown() { return maxErrorCountToShutDown; }

  public void setMaxErrorCountToShutDown(int maxErrorCountToShutDown) {
    this.maxErrorCountToShutDown = maxErrorCountToShutDown;
  }

  public String getDiscordToken() { return discordToken; }

  public MysqlData getMysql() { return mysql; }

  public WebServerData getWebServer() { return webServer; }

  public void setDiscordToken(String discordToken) {
    this.discordToken = discordToken;
  }

  public void setMysql(MysqlData mysql) { this.mysql = mysql; }

  public void setWebServer(WebServerData webServer) {
    this.webServer = webServer;
  }

  public static class Statistics {
    private int deleteJoins = 30;
    private int deleteLefts = 30;
    private int deleteMessageActivity = 7;
    private int deleteVoiceActivity = 7;

    public int getDeleteJoins() {
      return deleteJoins;
    }

    public void setDeleteJoins(int deleteJoins) {
      this.deleteJoins = deleteJoins;
    }

    public int getDeleteLefts() {
      return deleteLefts;
    }

    public void setDeleteLefts(int deleteLefts) {
      this.deleteLefts = deleteLefts;
    }

    public int getDeleteMessageActivity() {
      return deleteMessageActivity;
    }

    public void setDeleteMessageActivity(int deleteMessageActivity) {
      this.deleteMessageActivity = deleteMessageActivity;
    }

    public int getDeleteVoiceActivity() {
      return deleteVoiceActivity;
    }

    public void setDeleteVoiceActivity(int deleteVoiceActivity) {
      this.deleteVoiceActivity = deleteVoiceActivity;
    }
  }

  public static class Emotes {
    private long emoteOk = 767932772194451478L;
    private long emoteNo = 767950709722841098L;
    private long emoteNoPermission = 767932411198832640L;
    private long emotePlay = 768003585312555021L;
    private long emotePause = 768003585299447809L;

    public long getEmotePlay() { return emotePlay; }

    public void setEmotePlay(long emotePlay) { this.emotePlay = emotePlay; }

    public long getEmotePause() { return emotePause; }

    public void setEmotePause(long emotePause) { this.emotePause = emotePause; }

    public long getEmoteNoPermission() { return emoteNoPermission; }

    public void setEmoteNoPermission(long emoteNoPermission) {
      this.emoteNoPermission = emoteNoPermission;
    }

    public long getEmoteOk() { return emoteOk; }

    public void setEmoteOk(long emoteOk) { this.emoteOk = emoteOk; }

    public long getEmoteNo() { return emoteNo; }

    public void setEmoteNo(long emoteNo) { this.emoteNo = emoteNo; }
  }

  public static class Spotify {
    private String clientId = "your id here";
    private String clientSecret = "your secret here";

    public String getClientId() { return clientId; }

    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientSecret() { return clientSecret; }

    public void setClientSecret(String clientSecret) {
      this.clientSecret = clientSecret;
    }
  }

  public static class DefaultLimits {
    private int logLines = 50;
    private int events = 50;
    private int actionsPerEvent = 10;
    private int permissions = 20;
    private int perMinuteWebHookCalls = 10;
    private int perMinuteOutgoingWebHookCalls = 10;

    public int getPerMinuteWebHookCalls() { return perMinuteWebHookCalls; }

    public void setPerMinuteWebHookCalls(int perMinuteWebHookCalls) {
      this.perMinuteWebHookCalls = perMinuteWebHookCalls;
    }

    public int getPerMinuteOutgoingWebHookCalls() {
      return perMinuteOutgoingWebHookCalls;
    }

    public void
    setPerMinuteOutgoingWebHookCalls(int perMinuteOutgoingWebHookCalls) {
      this.perMinuteOutgoingWebHookCalls = perMinuteOutgoingWebHookCalls;
    }

    public int getPermissions() { return permissions; }

    public void setPermissions(int permissions) {
      this.permissions = permissions;
    }

    public int getLogLines() { return logLines; }

    public void setLogLines(int logLines) { this.logLines = logLines; }

    public int getEvents() { return events; }

    public void setEvents(int events) { this.events = events; }

    public int getActionsPerEvent() { return actionsPerEvent; }

    public void setActionsPerEvent(int actionsPerEvent) {
      this.actionsPerEvent = actionsPerEvent;
    }
  }

  public static class WebServerData {
    private int port = 8040;
    private int buffer = 1024;
    private String domain = "http://localhost:8040/";

    public String getDomain() { return domain; }

    public void setDomain(String domain) { this.domain = domain; }

    public int getPort() { return port; }

    public int getBuffer() { return buffer; }

    public void setPort(int port) { this.port = port; }

    public void setBuffer(int buffer) { this.buffer = buffer; }

    private WebServerData() {}
  }

  public static class MysqlData {
    private String server = "localhost:3306";
    private String user = "username";
    private String pass = "password";
    private String database = "database";
    private String prefix = "disc0rd_";

    public String getServer() { return server; }

    public String getDatabase() { return database; }

    public String getPass() { return pass; }

    public String getPrefix() { return prefix; }

    public String getUser() { return user; }

    public void setServer(String server) { this.server = server; }

    public void setDatabase(String database) { this.database = database; }

    public void setPass(String pass) { this.pass = pass; }

    public void setPrefix(String prefix) { this.prefix = prefix; }

    public void setUser(String user) { this.user = user; }

    private MysqlData() {}
  }

  public static class OwnerPanelData {
    private long ownerId = 441419741316251670L;
    private Integer timeOut = 3600;
    private Integer keyValidTimeOut = 60;
    private boolean secureCookie = true;

    public boolean isSecureCookie() { return secureCookie; }

    public void setSecureCookie(boolean secureCookie) {
      this.secureCookie = secureCookie;
    }

    public Integer getKeyValidTimeOut() { return keyValidTimeOut; }

    public void setKeyValidTimeOut(Integer keyValidTimeOut) {
      this.keyValidTimeOut = keyValidTimeOut;
    }

    public long getOwnerId() { return ownerId; }

    public void setOwnerId(long ownerId) { this.ownerId = ownerId; }

    public Integer getTimeOut() { return timeOut; }

    public void setTimeOut(Integer timeOut) { this.timeOut = timeOut; }
  }

  public static class AdminPanelData {
    private Integer timeOut = 3600;
    private Integer keyValidTimeOut = 300;
    private boolean secureCookie = true;

    public boolean isSecureCookie() { return secureCookie; }

    public void setSecureCookie(boolean secureCookie) {
      this.secureCookie = secureCookie;
    }

    public Integer getKeyValidTimeOut() { return keyValidTimeOut; }

    public void setKeyValidTimeOut(Integer keyValidTimeOut) {
      this.keyValidTimeOut = keyValidTimeOut;
    }

    public Integer getTimeOut() { return timeOut; }

    public void setTimeOut(Integer timeOut) { this.timeOut = timeOut; }
  }
}
