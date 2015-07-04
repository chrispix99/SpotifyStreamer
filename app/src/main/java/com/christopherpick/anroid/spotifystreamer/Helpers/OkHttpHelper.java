package com.christopherpick.anroid.spotifystreamer.Helpers;


import com.squareup.okhttp.OkHttpClient;

/**
 * Helper class to keep a singleton reference and single point to manage my network client.
 */
public class OkHttpHelper {
    private static OkHttpHelper instance = null;
    private OkHttpClient okHttpClient;

    protected OkHttpHelper() {
        // empty to prevent instantiating
    }

    public static OkHttpHelper getInstance() {
        if (instance == null) {
            instance = new OkHttpHelper();
        }
        return instance;
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }
}
