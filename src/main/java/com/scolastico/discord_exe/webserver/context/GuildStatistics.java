package com.scolastico.discord_exe.webserver.context;

import com.google.gson.Gson;
import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.mysql.ServerStatistics;
import com.scolastico.discord_exe.webserver.WebHandler;
import com.scolastico.discord_exe.webserver.WebHandler.WebHandlerRegistration;
import com.sun.net.httpserver.HttpExchange;
import java.util.HashMap;

@WebHandlerRegistration(context = {"/api/v1/guild/statistics/get"})
public class GuildStatistics implements WebHandler {
  @Override
  public String onWebServer(HttpExchange httpExchange) {
    try {
      Long guildId = GuildPanel.getIdFromAuthorization(httpExchange);
      if (guildId != null) {
        ServerStatistics statistics = Disc0rd.getMysql().getServerSettings(guildId).getStatistics();
        HashMap<String, Object> data = new HashMap<>();
        data.put("joins", statistics.getJoins());
        data.put("lefts", statistics.getLefts());
        data.put("messageActivity", statistics.getMessageActivity());
        data.put("voiceActivity", statistics.getVoiceActivity());
        return "{\"status\":\"ok\", \"data\":" + new Gson().toJson(data) + "}";
      }
      return "{\"status\":\"error\", \"error\":\"no auth\"}";
    } catch (Exception e) {
      ErrorHandler.getInstance().handle(e);
    }
    return "{\"status\":\"error\",\"error\":\"internal error\"}";
  }
}
