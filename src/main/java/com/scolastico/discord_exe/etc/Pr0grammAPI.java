package com.scolastico.discord_exe.etc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Pr0grammAPI {

  /*
  Uses:

    <dependency>
      <groupId>org.apache.httpcomponents</groupId>
      <artifactId>httpclient</artifactId>
      <version>4.5.10</version>
    </dependency>
    <dependency>
      <groupId>org.json</groupId>
      <artifactId>json</artifactId>
      <version>20190722</version>
    </dependency>

  */

  private static final String pr0_url = "https://pr0gramm.com/api/";
  private final CloseableHttpClient httpClient = HttpClients.createDefault();
  private String token;

  Pr0grammAPI(String username, String password) throws Pr0grammLoginError {
    try {
      HttpPost post = new HttpPost(pr0_url + "user/login");
      post.addHeader(HttpHeaders.ACCEPT, "application/json");
      post.addHeader(HttpHeaders.USER_AGENT, "Pr0gramm JAVA API Client");
      List<NameValuePair> urlParameters = new ArrayList<>();
      urlParameters.add(new BasicNameValuePair("name", username));
      urlParameters.add(new BasicNameValuePair("password", password));
      post.setEntity(new UrlEncodedFormEntity(urlParameters));
      try (CloseableHttpClient httpClient = HttpClients.createDefault();
           CloseableHttpResponse response = httpClient.execute(post)) {
        JSONObject object =
            new JSONObject(EntityUtils.toString(response.getEntity()));
        if (object.isEmpty()) {
          throw new Pr0grammLoginError("unknown error", 0);
        }
        object.has("success");
        if (object.getBoolean("success")) {
          Header[] headers = response.getHeaders("Set-Cookie");
          for (Header header : headers) {
            if (header.getValue().startsWith("me=")) {
              token = header.getValue();
            }
          }
        } else if (object.has("error")) {
          throw new Pr0grammLoginError(object.getString("error"), 1);
        } else {
          throw new Pr0grammLoginError("unknown error", 0);
        }
      }
      if (token == null)
        throw new Pr0grammLoginError("unknown error", 0);
    } catch (Pr0grammLoginError e) {
      throw e;
    } catch (Exception e) {
      throw new Pr0grammLoginError("unknown error", 0);
    }
  }

  Pr0grammAPI(String token) throws Pr0grammLoginError {
    this.token = token;
    String response = makeGetRequest("user/loggedin");
    if (response == null)
      throw new Pr0grammLoginError("unknown error", 0);
    JSONObject object = new JSONObject(response);
    if (object.isEmpty())
      throw new Pr0grammLoginError("unknown error", 0);
    if (!object.has("loggedIn"))
      throw new Pr0grammLoginError("unknown error", 0);
    if (!object.getBoolean("loggedIn"))
      throw new Pr0grammLoginError("login invalid", 2);
  }

  public String getToken() { return token; }

  private String makeGetRequest(String path) {
    try {
      HttpGet request = new HttpGet(pr0_url + path);
      request.addHeader(HttpHeaders.USER_AGENT, "Pr0gramm JAVA API Client");
      request.addHeader(HttpHeaders.ACCEPT, "application/json");
      request.addHeader("Cookie", token);
      CloseableHttpResponse response = httpClient.execute(request);
      String ret = EntityUtils.toString(response.getEntity());
      response.close();
      return ret;
    } catch (Exception ignored) {
    }
    return null;
  }

  public void close() throws IOException { httpClient.close(); }

  public Pr0grammUser getPr0grammUser(String name) throws Pr0grammApiError {
    try {
      String request = makeGetRequest(
          "profile/info?name=" + name.replaceAll("/[^A-Za-z0-9]/", "") +
          "&flags=0");
      if (request == null) {
        throw new Pr0grammApiError("response is null");
      }
      JSONObject object = new JSONObject(request);
      if (object.has("error")) {
        if (object.get("error") instanceof String) {
          if (object.getString("error").equalsIgnoreCase("not found") ||
              object.getString("error").equalsIgnoreCase("notFound"))
            return null;
          throw new Pr0grammApiError(object.getString("error"));
        }
      }
      ArrayList<Pr0grammUser.Badge> badges = new ArrayList<>();
      JSONArray badgeArray = object.getJSONArray("badges");
      for (int index = 0; index != badgeArray.length(); index++) {
        JSONObject tmpJsonObject = badgeArray.getJSONObject(index);
        badges.add(new Pr0grammUser.Badge(
            tmpJsonObject.getString("link"), tmpJsonObject.getString("image"),
            tmpJsonObject.getString("description"),
            tmpJsonObject.getLong("created")));
      }
      JSONObject userObject = object.getJSONObject("user");
      return new Pr0grammUser(
          userObject.getLong("id"), userObject.getString("name"),
          userObject.getLong("registered"), userObject.getLong("score"),
          userObject.getLong("banned"), userObject.getLong("inactive"),
          userObject.getInt("commentDelete"), userObject.getInt("itemDelete"),
          object.getInt("commentCount"), object.getInt("commentLikesCount"),
          object.getInt("uploadCount"), object.getBoolean("likesArePublic"),
          object.getInt("likeCount"), object.getInt("tagCount"),
          badges.toArray(new Pr0grammUser.Badge[0]), userObject.getInt("mark"));
    } catch (Pr0grammApiError e) {
      throw e;
    } catch (Exception e) {
      throw new Pr0grammApiError("Unknown Error: " + e.getMessage());
    }
  }

  public Pr0grammGetItemsRequestGenerator generateGetItemsRequestGenerator() {
    return new Pr0grammGetItemsRequestGenerator(this);
  }

  public Pr0grammPost getPr0grammPost(Long id) throws Pr0grammApiError {
    try {
      String request =
          makeGetRequest("items/get?flags=15&older=" + Long.toString(id + 5));
      if (request == null) {
        throw new Pr0grammApiError("response is null");
      }
      JSONObject object = new JSONObject(request);
      if (object.has("error")) {
        if (object.get("error") instanceof String) {
          throw new Pr0grammApiError(object.getString("error"));
        }
      }
      if (!object.has("items"))
        throw new Pr0grammApiError("response not valid");
      JSONArray array = object.getJSONArray("items");
      for (int index = 0; index != array.length(); index++) {
        JSONObject entry = array.getJSONObject(index);
        long tmpId = entry.getLong("id");
        if (tmpId == id) {
          return new Pr0grammPost(
              entry.getLong("id"), entry.getInt("promoted") != 0,
              entry.getLong("userId"), entry.getInt("up"), entry.getInt("down"),
              entry.getLong("created"),
              entry.getString("image").toUpperCase().endsWith(".PNG") ||
                      entry.getString("image").toUpperCase().endsWith(
                          ".JPEG") ||
                      entry.getString("image").toUpperCase().endsWith(".JPG") ||
                      entry.getString("image").toUpperCase().endsWith(".GIF")
                  ? "https://img.pr0gramm.com/" + entry.getString("image")
                  : "https://vid.pr0gramm.com/" + entry.getString("image"),
              "https://thumb.pr0gramm.com/" + entry.getString("thumb"),
              entry.getString("fullsize").isEmpty()
                  ? null
                  : "https://full.pr0gramm.com/" + entry.getString("fullsize"),
              entry.getInt("width"), entry.getInt("height"),
              entry.getBoolean("audio"), entry.getString("source"),
              entry.getInt("flags"), entry.getString("user"),
              entry.getInt("mark"), entry.getInt("gift"));
        }
      }
    } catch (Pr0grammApiError e) {
      throw e;
    } catch (Exception e) {
      throw new Pr0grammApiError("Unknown Error: " + e.getMessage());
    }
    return null;
  }

  public Pr0grammPost[] getPr0grammPosts(
      Pr0grammGetItemsRequestGenerator generator) throws Pr0grammApiError {
    try {
      ArrayList<Pr0grammPost> ret = new ArrayList<>();
      String request = makeGetRequest(generator.generate());
      if (request == null) {
        throw new Pr0grammApiError("response is null");
      }
      JSONObject object = new JSONObject(request);
      if (object.has("error")) {
        if (object.get("error") instanceof String) {
          throw new Pr0grammApiError(object.getString("error"));
        }
      }
      if (!object.has("items"))
        throw new Pr0grammApiError("response not valid");
      JSONArray array = object.getJSONArray("items");
      for (int index = 0; index != array.length(); index++) {
        JSONObject entry = array.getJSONObject(index);
        ret.add(new Pr0grammPost(
            entry.getLong("id"), entry.getInt("promoted") != 0,
            entry.getLong("userId"), entry.getInt("up"), entry.getInt("down"),
            entry.getLong("created"),
            entry.getString("image").toUpperCase().endsWith(".PNG") ||
                    entry.getString("image").toUpperCase().endsWith(".JPEG") ||
                    entry.getString("image").toUpperCase().endsWith(".JPG") ||
                    entry.getString("image").toUpperCase().endsWith(".GIF")
                ? "https://img.pr0gramm.com/" + entry.getString("image")
                : "https://vid.pr0gramm.com/" + entry.getString("image"),
            "https://thumb.pr0gramm.com/" + entry.getString("thumb"),
            entry.getString("fullsize").isEmpty()
                ? null
                : "https://full.pr0gramm.com/" + entry.getString("fullsize"),
            entry.getInt("width"), entry.getInt("height"),
            entry.getBoolean("audio"), entry.getString("source"),
            entry.getInt("flags"), entry.getString("user"),
            entry.getInt("mark"), entry.getInt("gift")));
      }
      return ret.toArray(new Pr0grammPost[0]);
    } catch (Pr0grammApiError e) {
      throw e;
    } catch (Exception e) {
      throw new Pr0grammApiError("Unknown Error: " + e.getMessage());
    }
  }

  public static class Pr0grammLoginError extends Exception {
    private int type = 0;

    public Pr0grammLoginError() {}

    public Pr0grammLoginError(String message) { super(message); }

    public Pr0grammLoginError(String message, int type) {
      super(message);
      this.type = type;
    }

    public int getType() { return type; }
  }

  public static class Pr0grammApiError extends Exception {
    public Pr0grammApiError() {}

    public Pr0grammApiError(String message) { super(message); }
  }

  public static class Pr0grammPost {
    private long id;
    private boolean promoted;
    private long userId;
    private int up;
    private int down;
    private long created;
    private String image;
    private String thumbnail;
    private String full;
    private int width;
    private int height;
    private boolean audio;
    private String source;
    private int flags;
    private String user;
    private int mark;
    private int gift;

    private Pr0grammPost(long id, boolean promoted, long userId, int up,
                         int down, long created, String image, String thumbnail,
                         String full, int width, int height, boolean audio,
                         String source, int flags, String user, int mark,
                         int gift) {
      this.id = id;
      this.promoted = promoted;
      this.userId = userId;
      this.up = up;
      this.down = down;
      this.created = created;
      this.image = image;
      this.thumbnail = thumbnail;
      this.full = full;
      this.width = width;
      this.height = height;
      this.audio = audio;
      this.source = source;
      this.flags = flags;
      this.user = user;
      this.mark = mark;
      this.gift = gift;
    }

    public long getId() { return id; }

    public boolean isPromoted() { return promoted; }

    public long getUserId() { return userId; }

    public int getUp() { return up; }

    public int getDown() { return down; }

    public long getCreated() { return created; }

    public String getImage() { return image; }

    public String getThumbnail() { return thumbnail; }

    public String getFull() { return full; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public boolean isAudio() { return audio; }

    public String getSource() { return source; }

    public int getFlags() { return flags; }

    public String getUser() { return user; }

    public int getMark() { return mark; }

    public int getGift() { return gift; }
  }

  public static class Pr0grammFlagCalculator {
    boolean sfw = true;
    boolean nsfp = false;
    boolean nsfw = false;
    boolean nsfl = false;

    public boolean isNsfp() { return nsfp; }

    public void setNsfp(boolean nsfp) { this.nsfp = nsfp; }

    public boolean isSfw() { return sfw; }

    public void setSfw(boolean sfw) { this.sfw = sfw; }

    public boolean isNsfw() { return nsfw; }

    public void setNsfw(boolean nsfw) { this.nsfw = nsfw; }

    public boolean isNsfl() { return nsfl; }

    public void setNsfl(boolean nsfl) { this.nsfl = nsfl; }

    public int calculate() {
      int ret = 0;
      if (sfw)
        ret += 1;
      if (nsfw)
        ret += 2;
      if (nsfl)
        ret += 4;
      if (nsfp)
        ret += 8;
      return ret;
    }
  }

  public static class Pr0grammUser {
    private long id;
    private String name;
    private long registered;
    private long score;
    private long banned;
    private long inactive;
    private long commentsDeleted;
    private long itemsDeleted;
    private long commentCount;
    private long commentLikesCount;
    private long uploadCount;
    private boolean likesArePublic;
    private long likesCount;
    private long tagCount;
    private Badge[] badges;
    private int mark;

    public Pr0grammUser(long id, String name, long registered, long score,
                        long banned, long inactive, long commentsDeleted,
                        long itemsDeleted, long commentCount,
                        long commentLikesCount, long uploadCount,
                        boolean likesArePublic, long likesCount, long tagCount,
                        Badge[] badges, int mark) {
      this.id = id;
      this.name = name;
      this.registered = registered;
      this.score = score;
      this.banned = banned;
      this.inactive = inactive;
      this.commentsDeleted = commentsDeleted;
      this.itemsDeleted = itemsDeleted;
      this.commentCount = commentCount;
      this.commentLikesCount = commentLikesCount;
      this.uploadCount = uploadCount;
      this.likesArePublic = likesArePublic;
      this.likesCount = likesCount;
      this.tagCount = tagCount;
      this.badges = badges;
      this.mark = mark;
    }

    public long getId() { return id; }

    public String getName() { return name; }

    public long getRegistered() { return registered; }

    public long getScore() { return score; }

    public long getBanned() { return banned; }

    public long getInactive() { return inactive; }

    public long getCommentsDeleted() { return commentsDeleted; }

    public long getItemsDeleted() { return itemsDeleted; }

    public long getCommentCount() { return commentCount; }

    public long getCommentLikesCount() { return commentLikesCount; }

    public long getUploadCount() { return uploadCount; }

    public boolean isLikesArePublic() { return likesArePublic; }

    public long getLikesCount() { return likesCount; }

    public long getTagCount() { return tagCount; }

    public Badge[] getBadges() { return badges; }

    public int getMark() { return mark; }

    public static class Badge {
      private String link;
      private String image;
      private String description;
      private long created;

      private Badge(String link, String image, String description,
                    long created) {
        this.link = link;
        this.image = image;
        this.description = description;
        this.created = created;
      }

      public String getLink() { return link; }

      public String getImage() { return image; }

      public String getDescription() { return description; }

      public long getCreated() { return created; }
    }
  }

  public static class Pr0grammGetItemsRequestGenerator {
    Pr0grammAPI api;

    private Long older = null;
    private Pr0grammFlagCalculator flagCalculator = null;
    private boolean promoted = false;
    private String user = null;
    private String likes = null;
    private String tags = null;
    private Long newer = null;

    private Pr0grammGetItemsRequestGenerator(Pr0grammAPI api) {
      this.api = api;
    }

    private String generate() {
      String ret = "";
      if (older != null)
        ret += "&older=" + older.toString();
      if (newer != null)
        ret += "&newer=" + newer.toString();
      if (flagCalculator != null)
        ret += "&flags=" + flagCalculator.calculate();
      if (promoted)
        ret += "&promoted=1";
      else
        ret += "&promoted=0";
      if (user != null)
        ret += "&user=" + user;
      if (likes != null)
        ret += "&likes=" + likes;
      try {
        if (tags != null)
          ret += "&tags=" +
                 URLEncoder.encode(tags, StandardCharsets.UTF_8.toString());
      } catch (UnsupportedEncodingException ignored) {
      }
      return "items/get?" + ret.substring(1);
    }

    public Pr0grammAPI getApi() { return api; }

    public Long getOlder() { return older; }

    public void setOlder(Long older) { this.older = older; }

    public void setUser(String user) { this.user = user; }

    public void setLikes(String likes) { this.likes = likes; }

    public void setTags(String tags) { this.tags = tags; }

    public void setNewer(Long newer) { this.newer = newer; }

    public void setFlagCalculator(Pr0grammFlagCalculator flagCalculator) {
      this.flagCalculator = flagCalculator;
    }

    public void setPromoted(boolean promoted) { this.promoted = promoted; }

    public Pr0grammFlagCalculator getFlagCalculator() { return flagCalculator; }

    public boolean isPromoted() { return promoted; }

    public String getUser() { return user; }

    public String getLikes() { return likes; }

    public String getTags() { return tags; }

    public Long getNewer() { return newer; }
  }
}
