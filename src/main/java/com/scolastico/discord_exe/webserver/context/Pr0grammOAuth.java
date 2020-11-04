package com.scolastico.discord_exe.webserver.context;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.config.Pr0grammConfigDataStore;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.Pr0grammManager;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.webserver.WebHandler;
import com.sun.net.httpserver.HttpExchange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

@WebHandler.WebHandlerRegistration(context = {"/api/v1/pr0gramm/oauth*"})
public class Pr0grammOAuth implements WebHandler {

  private static HashMap<String, AuthKeyInformation> authKeys = new HashMap<>();
  private static final String success_page =
      "<!doctype html><title>Success!</title><style>body{text-align: center; padding: 150px;}h1{font-size: 50px;}body{font: 20px Helvetica, sans-serif; color: #333;}article{display: block; text-align: left; width: 650px; margin: 0 auto;}a{color: #dc8100; text-decoration: none;}a:hover{color: #333; text-decoration: none;}</style><article> <h1>Success!</h1> <div> <p>You have been successfully logged in via Pr0gramm.com! You can now safely leave this page and concentrate on Discord again!</p></div></article>";
  private static final String error_page =
      "<!doctype html><title>Error!</title><style>body{text-align: center; padding: 150px;}h1{font-size: 50px;}body{font: 20px Helvetica, sans-serif; color: #333;}article{display: block; text-align: left; width: 650px; margin: 0 auto;}a{color: #dc8100; text-decoration: none;}a:hover{color: #333; text-decoration: none;}</style><article> <h1>Sorry,</h1> <div> <p>an error has occurred. You can try again by requesting a new login link on Discord.</p></div></article>";

  public static String getAuthKey(long guildId, long memberId) {
    String key;
    clearAuthKeys();
    do {
      key = Tools.getInstance().getAlphaNumericString(16);
    } while (authKeys.containsKey(key));
    authKeys.put(key, new AuthKeyInformation(key, guildId, memberId));
    return key;
  }

  private static void clearAuthKeys() {
    ArrayList<String> toDelete = new ArrayList<>();
    for (String key : authKeys.keySet()) {
      if (authKeys.get(key).getValidUntil() <=
          (System.currentTimeMillis() / 1000L)) {
        toDelete.add(key);
      }
    }
    for (String key : toDelete) {
      authKeys.remove(key);
    }
  }

  @Override
  public String onWebServer(HttpExchange httpExchange) {
    try {
      HashMap<String, String> getVariables =
          Tools.getInstance().getGetValuesFromHttpExchange(httpExchange);
      if (getVariables.containsKey("state") &&
          getVariables.containsKey("authCode") &&
          getVariables.containsKey("userId")) {
        String state = getVariables.get("state");
        String authCode = getVariables.get("authCode");
        String userId = getVariables.get("userId");
        clearAuthKeys();
        if (authKeys.containsKey(state)) {
          AuthKeyInformation authKeyInformation = authKeys.get(state);
          authKeys.remove(state);

          String token = getAuthToken(authCode, userId);
          if (token == null)
            return error_page;

          String username = getUsername(token);
          if (username == null)
            return error_page;

          Pr0grammManager.getInstance().authGuild(authKeyInformation.guildId,
                                                  username,
                                                  authKeyInformation.memberId);

          return success_page;
        }
      }
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
    return error_page;
  }

  private String getUsername(String authToken) {

    try {

      CloseableHttpClient httpClient = Disc0rd.getHttpClient();

      HttpPost post = new HttpPost("https://pr0gramm.com/api/user/name");
      post.addHeader(HttpHeaders.USER_AGENT, "Disc0rd.exe JAVA BOT");
      post.addHeader("Cookie",
                     Pr0grammManager.getInstance().getPr0grammAPI().getToken());
      post.addHeader("pr0-api-key", authToken);

      CloseableHttpResponse response = httpClient.execute(post);

      JSONObject responseJSON =
          new JSONObject(EntityUtils.toString(response.getEntity()));

      if (responseJSON.has("error")) {
        if (responseJSON.get("error") instanceof String) {
          return null;
        }
      }

      if (responseJSON.has("name")) {
        return responseJSON.getString("name");
      }

    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }

    return null;
  }

  private String getAuthToken(String authCode, String userId) {

    try {

      CloseableHttpClient httpClient = Disc0rd.getHttpClient();

      HttpPost post = new HttpPost("https://pr0gramm.com/api/user/authtoken");
      post.addHeader(HttpHeaders.USER_AGENT, "Disc0rd.exe JAVA BOT");
      post.addHeader("Cookie",
                     Pr0grammManager.getInstance().getPr0grammAPI().getToken());
      List<NameValuePair> urlParameters = new ArrayList<>();

      Pr0grammConfigDataStore.OAuth oAuthConfig =
          Pr0grammManager.getInstance().getConfig().getOAuth();

      urlParameters.add(new BasicNameValuePair("authCode", authCode));
      urlParameters.add(new BasicNameValuePair("userId", userId));
      urlParameters.add(
          new BasicNameValuePair("clientId", oAuthConfig.getClientId()));
      urlParameters.add(
          new BasicNameValuePair("clientSecret", oAuthConfig.getSecret()));

      post.setEntity(new UrlEncodedFormEntity(urlParameters));
      CloseableHttpResponse response = httpClient.execute(post);

      JSONObject responseJSON =
          new JSONObject(EntityUtils.toString(response.getEntity()));

      if (responseJSON.has("error")) {
        if (responseJSON.get("error") instanceof String) {
          return null;
        }
      }

      if (responseJSON.has("accessToken")) {
        return responseJSON.getString("accessToken");
      }

    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }

    return null;
  }

  private static class AuthKeyInformation {
    private final long validUntil;
    private final String key;
    private final long guildId;
    private final long memberId;

    public AuthKeyInformation(String key, long guildId, long memberId) {
      this.key = key;
      this.guildId = guildId;
      this.memberId = memberId;
      this.validUntil =
          (System.currentTimeMillis() / 1000L) + Pr0grammManager.getInstance()
                                                     .getConfig()
                                                     .getOAuth()
                                                     .getKeyValidUntil();
    }

    public long getMemberId() { return memberId; }

    public long getValidUntil() { return validUntil; }

    public String getKey() { return key; }

    public long getGuildId() { return guildId; }
  }
}
