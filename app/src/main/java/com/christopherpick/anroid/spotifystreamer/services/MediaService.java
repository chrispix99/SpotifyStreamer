package com.christopherpick.anroid.spotifystreamer.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.SeekBar;
import android.widget.Toast;
import com.christopherpick.anroid.spotifystreamer.R;
import com.christopherpick.anroid.spotifystreamer.utils.ImageUtils;
import com.christopherpick.anroid.spotifystreamer.utils.ShowToastMessage;
import kaaes.spotify.webapi.android.models.Tracks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chrispix on 7/12/15.
 */
public class MediaService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private List<IMediaServiceCallbacks> callbackList = new ArrayList<>();
    private Tracks tracks;
    private int currentTrack;
    private MediaPlayer mediaPlayer;
    private boolean paused = false;
    private Handler seekHandler = new Handler();


    @Override
    public void onPrepared(MediaPlayer mp) {
        playMediaPlayer();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        forwardTrack();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    private void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void pauseMediaPlayer() {
        if (mediaPlayer != null) {
            paused = true;
            mediaPlayer.pause();
        }
    }

    /**
     * Used to start playing/resuming the current track. // was startPlaying
     *
     * @return
     */
    private boolean playMediaPlayer() {
        if (mediaPlayer != null) {
            paused = false;
            mediaPlayer.start();
            updateSeekBars();
            configureMaxSeek();
            return true;
        } else {
            return playTrack();
        }
    }

    /**
     * Sets up and configures the media player for playing a track.
     *
     * @return
     */
    private boolean playTrack() {
        try {
            String url = tracks.tracks.get(currentTrack).preview_url; // your URL here
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            ShowToastMessage.showToast(getApplicationContext(), R.string.error_playing_track);
            return false;
        }
    }

    Runnable run = new Runnable() {

        @Override
        public void run() {
            updateSeekBars();
        }
    };

    private void updateSeekBars() {
        for (int i = 0; i < callbackList.size(); i++) {
            callbackList.get(i).getSeekBar().setProgress(mediaPlayer.getCurrentPosition());
            callbackList.get(i).currentTime(String.valueOf(mediaPlayer.getCurrentPosition()/1000) + "s");
            if (mediaPlayer.isPlaying()) {
                seekHandler.postDelayed(run, 1000);
            }
        }
    }

    private void configureMaxSeek() {
        for (int i = 0; i < callbackList.size(); i++) {
            if (mediaPlayer != null) {
                callbackList.get(i).getSeekBar().setMax(mediaPlayer.getDuration());
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Set the current tracks object, this should only be called
     * from the tracks list when user wants to play a track.
     *
     * @param tracks
     */
    public void setTracks(Tracks tracks, int currentTrack) {
        this.tracks = tracks;
        this.currentTrack = currentTrack;
        trackChange();
    }

    /**
     * Return the artist name currently selected
     *
     * @return
     */
    public String getArtistName() {
        return tracks.tracks.get(currentTrack).artists.get(0).name;
    }

    /**
     * Return the album name currently selected
     *
     * @return
     */
    public String getAlbumName() {
        return tracks.tracks.get(currentTrack).album.name;
    }

    /**
     * Return the full size image URL currently selected
     *
     * @return
     */
    public String getFullSizeImageUrl() {
        return ImageUtils.getBestImageUrl(tracks.tracks.get(currentTrack).album.images, 600);
    }

    /**
     * Returns the thumbnail image URL currently selected
     *
     * @return
     */
    public String getThumbnailImageUrl() {
        return ImageUtils.getBestImageUrl(tracks.tracks.get(currentTrack).album.images, 200);
    }

    /**
     * Returns the track name currently selected
     *
     * @return
     */
    public String getTrackName() {
        return tracks.tracks.get(currentTrack).name;
    }

    /**
     * Returns the track length currently selected
     *
     * @return
     */
    public String getTrackLength() {
        return String.valueOf((int)(tracks.tracks.get(currentTrack).duration_ms * 0.0001)) + "s";
    }

    public void setCurrentPosition(int pos) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(pos);
        }
    }

    @Override
    public void onDestroy() {
        unregisterAllCallbacks();
        unloadMediaPlayer();
    }

    /**
     * Notifies all the registered listeners of a track change
     */
    private void notifyTrackChange() {
        for (int i = 0; i < callbackList.size(); i++) {
            callbackList.get(i).mediaChanged();
        }
    }

    /**
     * Returns if the media player is currently playing.
     *
     * @return
     */
    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        } else {
            return false;
        }
    }

    /**
     * Method to register the media service callbacks to the UI so we can call back with events.
     *
     * @param callback
     */
    public void registerCallback(IMediaServiceCallbacks callback) {
        if (callbackList.indexOf(callback) >= 0) {
            throw new IllegalArgumentException("callback already registered, unregister first");
        } else {
            callbackList.add(callback);
        }
    }

    public void unregisterCallback(IMediaServiceCallbacks callback) {
        callbackList.remove(callback);
    }

    private void unregisterAllCallbacks() {
        callbackList.clear();
    }

    /**
     * Advance the track forward.
     */
    public void forwardTrack() {
        currentTrack += 1;
        if (currentTrack >= tracks.tracks.size()) {
            currentTrack = 0;
        }
        trackChange();
    }

    /**
     * Advance track backwards.
     */
    public void backTrack() {
        currentTrack -= 1;
        if (currentTrack < 0) {
            currentTrack = tracks.tracks.size() - 1;
        }
        trackChange();
    }

    /**
     * Pause the track
     */
    public void pauseTrack() {
        pauseMediaPlayer();
    }

    /**
     * Play / resume the track
     */
    public void resumeTrack() {
        playMediaPlayer();
    }

    /**
     * returns if the current track is paused or not
     *
     * @return
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Stops the current track
     */
    public void stopTrack() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * This unloads the media player and notifies there was a track change,
     * then if the previous track was not paused, it will play the new track.
     */
    private void trackChange() {
        unloadMediaPlayer();
        notifyTrackChange();
        if (!paused) {
            playTrack();
        }

    }

    /**
     * Unloads the media player, this needs to be called to clean up when the service
     * is no longer needed.
     */
    private void unloadMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * Callbacks to notify the UI of certain events.
     */
    public interface IMediaServiceCallbacks {
        public void mediaChanged();
        public SeekBar getSeekBar();
        public void currentTime(String time);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public MediaService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MediaService.this;
        }
    }
}
