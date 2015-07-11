package com.christopherpick.anroid.spotifystreamer.helpers;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Helper class to just keep a single reference of the spotify service
 * accessible to the app so we don't keep instantiating new instances.
 */
public class SpotifyHelper {

    private static SpotifyHelper instance = null;
    private SpotifyApi api;
    private SpotifyService spotify;

    protected SpotifyHelper() {
        // only to defeat instanciation by others
    }

    public static SpotifyHelper getInstance() {
        if (instance == null) {
            instance = new SpotifyHelper();
        }
        return instance;
    }

    public SpotifyService getSpotifyServiceInstancce() {
        if (api == null) {
            api = new SpotifyApi();
        }
        if (spotify == null) {
            spotify = api.getService();
        }
        return spotify;
    }
}
