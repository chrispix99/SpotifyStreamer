package com.christopherpick.anroid.spotifystreamer;

import android.app.Application;
import com.christopherpick.anroid.spotifystreamer.helpers.OkHttpHelper;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;

/**
 * Spotify Application class to add hooks into custom application context tasks, such as setting up
 * stetho.
 */
public class SpotifyApplication extends Application {
    public void onCreate() {
        super.onCreate();

        // Add in debug related hooks into the application
        if (BuildConfig.DEBUG) {
            InitializeStetho();
        }
    }

    /**
     * Initializes stetho library, and adds the Stetho interceptor to the okHttp calls. This can only
     * be used from debug builds.
     */
    private void InitializeStetho() {
        if (!BuildConfig.DEBUG) {
            throw new IllegalStateException("Can only be called from debug builds");
        }
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
        OkHttpHelper.getInstance().getOkHttpClient().networkInterceptors().add(new StethoInterceptor());
    }
}
