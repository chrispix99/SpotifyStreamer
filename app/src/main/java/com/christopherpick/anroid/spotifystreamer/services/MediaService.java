package com.christopherpick.anroid.spotifystreamer.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.*;
import android.os.Process;
import android.widget.MediaController;
import android.widget.Toast;
import com.christopherpick.anroid.spotifystreamer.R;
import com.christopherpick.anroid.spotifystreamer.utils.ShowToastMessage;

import java.util.Random;

/**
 * Created by chrispix on 7/12/15.
 */
public class MediaService extends Service implements MediaPlayer.OnPreparedListener {


    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        MediaService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MediaService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** method for clients */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onDestroy() {
        ShowToastMessage.showToast(this.getApplicationContext(), R.string.media_service_stopped, Toast.LENGTH_SHORT);
    }




}
