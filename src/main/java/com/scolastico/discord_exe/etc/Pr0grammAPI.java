package com.scolastico.discord_exe.etc;


import org.apache.http.Header;
import org.apache.http.HttpEntity;
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
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Pr0grammAPI {

    /*
    Uses:

    <dependency>
		<groupId>org.apache.httpcomponents</groupId>
		<artifactId>httpclient</artifactId>
		<version>4.5.10</version>
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
                JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
                if (object.isEmpty()) {
                    throw new Pr0grammLoginError("unknown error", 0);
                }
                object.has("success");
                if (object.getBoolean("success")) {
                    Header[] headers = response.getHeaders("Set-Cookie");
                    for (Header header:headers) {
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
            if (token == null) throw new Pr0grammLoginError("unknown error", 0);
        } catch (Pr0grammLoginError e) {
            throw e;
        } catch (Exception e) {
            throw new Pr0grammLoginError("unknown error", 0);
        }
    }

    Pr0grammAPI(String token) throws Pr0grammLoginError {
        this.token = token;
        String response = makeGetRequest("user/loggedin");
        if (response == null) throw new Pr0grammLoginError("unknown error", 0);
        JSONObject object = new JSONObject(response);
        if (object.isEmpty()) throw new Pr0grammLoginError("unknown error", 0);
        if (!object.has("loggedIn")) throw new Pr0grammLoginError("unknown error", 0);
        if (!object.getBoolean("loggedIn")) throw new Pr0grammLoginError("login invalid", 2);
    }

    public String getToken() {
        return token;
    }

    private String makeGetRequest(String path) {
        try {
            HttpGet request = new HttpGet(pr0_url + path);
            request.addHeader(HttpHeaders.USER_AGENT, "Pr0gramm JAVA API Client");
            request.addHeader(HttpHeaders.ACCEPT, "application/json");
            request.addHeader("Cookie", token);
            CloseableHttpResponse response = httpClient.execute(request);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception ignored) {}
        return null;
    }

    private void close() throws IOException {
        httpClient.close();
    }

    public static class Pr0grammLoginError extends Exception {
        private int type = 0;

        public Pr0grammLoginError() {}

        public Pr0grammLoginError(String message) {
            super(message);
        }

        public Pr0grammLoginError(String message, int type) {
            super(message);
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

    public static class Pr0grammApiError extends Exception {
        public Pr0grammApiError() {}

        public Pr0grammApiError(String message) {
            super(message);
        }
    }

}
