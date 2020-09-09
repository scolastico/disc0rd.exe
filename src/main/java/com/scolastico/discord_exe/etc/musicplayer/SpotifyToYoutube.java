package com.scolastico.discord_exe.etc.musicplayer;

import com.scolastico.discord_exe.Disc0rd;
import com.scolastico.discord_exe.etc.ErrorHandler;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;

import java.net.URI;
import java.util.ArrayList;

public class SpotifyToYoutube {

    private SpotifyApi spotifyApi;
    private ClientCredentialsRequest clientCredentialsRequest;
    private ClientCredentials clientCredentials;
    private static SpotifyToYoutube instance = null;
    private long expires = 0;

    public static SpotifyToYoutube getInstance() {
        if (instance == null) {
            instance = new SpotifyToYoutube();
        }
        return instance;
    }

    private SpotifyToYoutube() {
        try {
            spotifyApi = new SpotifyApi.Builder()
                    .setClientId(Disc0rd.getConfig().getSpotify().getClientId())
                    .setClientSecret(Disc0rd.getConfig().getSpotify().getClientSecret())
                    .setRedirectUri(new URI(Disc0rd.getConfig().getWebServer().getDomain()))
                    .build();
            clientCredentialsRequest = spotifyApi.clientCredentials().build();
            clientCredentials = clientCredentialsRequest.execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
            expires = clientCredentials.getExpiresIn() + getUnixTimeStamp();
        } catch (Exception e) {
            ErrorHandler.getInstance().handleFatal(e);
        }
    }

    public String[] spotifyPlaylistToString(String id) {
        ArrayList<String> ret = new ArrayList<>();
        try {
            if (checkToken()) {
                Playlist playlist = spotifyApi.getPlaylist(id).build().execute();
                for (PlaylistTrack track:playlist.getTracks().getItems()) {
                    if (!track.getIsLocal()) {
                        Track tmp = ((Track) track.getTrack());
                        if (tmp != null) {
                            StringBuilder artist = new StringBuilder();
                            for (ArtistSimplified artists:tmp.getArtists()) {
                                artist.append(artists.getName()).append(", ");
                            }
                            String a = artist.toString();
                            if (a.endsWith(", ")) a = a.substring(0, a.length()-2);
                            ret.add("ytsearch:" + tmp.getName() + " - " + artist.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return ret.toArray(new String[0]);
    }

    public String spotifyToString(String id) {
        try {
            if (checkToken()) {
                Track track = spotifyApi.getTrack(id).build().execute();
                if (track != null) {
                    StringBuilder artist = new StringBuilder();
                    for (ArtistSimplified artists:track.getArtists()) {
                        artist.append(artists.getName()).append(", ");
                    }
                    String tmp = artist.toString();
                    if (tmp.endsWith(", ")) tmp = tmp.substring(0, tmp.length()-3);
                    return "ytsearch:" + track.getName() + " - " + artist.toString();
                }
            }
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return null;
    }

    private boolean checkToken() {
        try {
            if (expires - getUnixTimeStamp() <= 30) {
                clientCredentials = clientCredentialsRequest.execute();
                spotifyApi.setAccessToken(clientCredentials.getAccessToken());
                expires = clientCredentials.getExpiresIn() + getUnixTimeStamp();
            }
            return true;
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return false;
    }

    private Long getUnixTimeStamp() {
        return System.currentTimeMillis() / 1000L;
    }

}
