package com.scolastico.discord_exe.etc;

import com.scolastico.discord_exe.Disc0rd;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class MEE6Api {

    private static MEE6Api instance = null;
    private CloseableHttpClient httpClient;

    public static MEE6Api getInstance() {
        if (instance == null) {
            instance = new MEE6Api();
        }
        return instance;
    }

    private MEE6Api() {
        httpClient = HttpClients.createDefault();
        Disc0rd.addOnExitRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    httpClient.close();
                } catch (Exception ignored) {}
            }
        });
    }

    public HashMap<Long, Long> getXP(long guildId) {
        HashMap<Long, Long> ret = new HashMap<>();
        JDA jda = Disc0rd.getJda();
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return ret;
        try {
            HttpGet request = new HttpGet("https://mee6.xyz/api/plugins/levels/leaderboard/" + guildId);

            request.addHeader(HttpHeaders.USER_AGENT, "JAVA API CLIENT FOR MEE6");

            CloseableHttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {

                HttpEntity entity = response.getEntity();
                Header headers = entity.getContentType();

                String result = EntityUtils.toString(entity);

                JSONObject resultJSON = new JSONObject(result);

                if (resultJSON.has("players")) {

                    JSONArray players = resultJSON.getJSONArray("players");

                    for (int index = 0; players.length() > index; index++) {

                        JSONObject player = players.getJSONObject(index);

                        ret.put(Long.parseLong(player.getString("id")), player.getLong("xp"));

                    }

                }

            }

            response.close();

        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return ret;
    }

}
