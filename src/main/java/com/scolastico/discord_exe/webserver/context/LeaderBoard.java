package com.scolastico.discord_exe.webserver.context;

import com.google.gson.Gson;
import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.scolastico.discord_exe.etc.Tools;
import com.scolastico.discord_exe.mysql.MysqlHandler;
import com.scolastico.discord_exe.mysql.ServerSettings;
import com.scolastico.discord_exe.webserver.WebHandler;
import com.sun.net.httpserver.HttpExchange;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.*;

@WebHandler.WebHandlerRegistration(context = {"/api/v1/leaderboard/img/*", "/api/v1/leaderboard/get/*"})
public class LeaderBoard implements WebHandler {
    @Override
    public String onWebServer(HttpExchange httpExchange) {
        try {
            if (httpExchange.getRequestURI().getPath().startsWith("/api/v1/leaderboard/img/")) {
                return img(httpExchange);
            } else if (httpExchange.getRequestURI().getPath().startsWith("/api/v1/leaderboard/get/")) {
                return get(httpExchange);
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
            return "{\"status\":\"error\",\"error\":\"internal server error\"}";
        }
        return null;
    }

    private String img(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath().replaceFirst("/api/v1/leaderboard/img/", "");
            if (path.split("/").length == 2) {
                String guildId = path.split("/")[0];
                String memberId = path.split("/")[1];
                JDA jda = Disc0rd.getJda();
                Guild guild = jda.getGuildById(guildId);
                if (guild != null) {
                    Member member = guild.getMemberById(memberId);
                    if (member != null) {
                        httpExchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8;");
                        return Tools.getInstance().getLeaderboardBannerSVG(guild, member);
                    }
                }
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
        return "{\"status\":\"error\",\"error\":\"guild and/or user not found\"}";
    }

    private String get(HttpExchange httpExchange) {
        try {
            String guildId = httpExchange.getRequestURI().getPath().replaceFirst("/api/v1/leaderboard/get/", "");
            if (guildId.equals("")) return "{\"status\":\"error\",\"error\":\"guild not found\"}";
            JDA jda = Disc0rd.getJda();
            Guild guild = jda.getGuildById(guildId);
            if (guild != null) {
                httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
                ServerSettings settings = Disc0rd.getMysql().getServerSettings(guild.getIdLong());
                HashMap<Long, Long> users = settings.getLeaderboard().getUsers();
                HashMap<Integer, ArrayList<LeaderBoardData>> data = new HashMap<>();
                for (Long id:users.keySet()) {
                    try {
                        Member member = guild.getMemberById(id);
                        if (member != null) if (!member.isFake()) if (!member.getUser().isBot()) {
                            long xp = users.get(id);
                            int level = Tools.getInstance().getLeaderboardLevel(xp);
                            int position = Tools.getInstance().getLeaderboardPlace(guild, member);
                            LeaderBoardData tmp = new LeaderBoardData(
                                    member.getUser().getName(),
                                    member.getUser().getAsTag().split("#")[1],
                                    (member.getUser().getAvatarUrl() == null ? "https://discord.com/assets/28174a34e77bb5e5310ced9f95cb480b.png" : member.getUser().getAvatarUrl()),
                                    member.getId(),
                                    xp,
                                    level,
                                    Tools.getInstance().getLeaderboardNextLevelXP(xp),
                                    Tools.getInstance().getLeaderboardLevelXP(level),
                                    position
                            );
                            if (data.containsKey(position)) {
                                data.get(position).add(tmp);
                            } else {
                                ArrayList<LeaderBoardData> list = new ArrayList<>();
                                list.add(tmp);
                                data.put(position, list);
                            }
                        }
                    } catch (Exception e) {
                        ErrorHandler.getInstance().handle(e);
                    }
                }
                StringBuilder ret = new StringBuilder();
                Gson gson = new Gson();
                List<Integer> places = new ArrayList<>(data.keySet());
                Collections.sort(places);
                for (Integer place:places) {
                    ArrayList<LeaderBoardData> list = data.get(place);
                    for (LeaderBoardData tmp:list) {
                        ret.append(",").append(gson.toJson(tmp));
                    }
                }
                return "{\"status\":\"ok\",\"leaderboard\":[" + ret.toString().replaceFirst(",", "") + "]}";
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8;");
        return "{\"status\":\"error\",\"error\":\"guild not found\"}";
    }

    private static class LeaderBoardData {

        private final String name;
        private final String tag;
        private final String picture;
        private final String id;
        private final long xp;
        private final int level;
        private final long nextLevelXP;
        private final long lastLevelXP;
        private final int place;

        public LeaderBoardData(String name, String tag, String picture, String id, long xp, int level, long nextLevelXP, long lastLevelXP, int place) {
            this.name = name;
            this.tag = tag;
            this.picture = picture;
            this.id = id;
            this.xp = xp;
            this.level = level;
            this.nextLevelXP = nextLevelXP;
            this.lastLevelXP = lastLevelXP;
            this.place = place;
        }

        public String getName() {
            return name;
        }

        public String getTag() {
            return tag;
        }

        public String getPicture() {
            return picture;
        }

        public String getId() {
            return id;
        }

        public long getXp() {
            return xp;
        }

        public int getLevel() {
            return level;
        }

        public long getNextLevelXP() {
            return nextLevelXP;
        }

        public long getLastLevelXP() {
            return lastLevelXP;
        }

        public int getPlace() {
            return place;
        }

    }

}
