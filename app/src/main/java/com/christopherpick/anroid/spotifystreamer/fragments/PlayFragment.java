package com.christopherpick.anroid.spotifystreamer.fragments;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.christopherpick.anroid.spotifystreamer.R;
import kaaes.spotify.webapi.android.models.Tracks;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by chrispix on 7/11/15.
 */
public class PlayFragment extends DialogFragment implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    private static final String ARG_DEFAULT_TRACK = "default_track";
    private static final String ARG_TRACKS = "tracks";

    private TextView artistName;
    private TextView albumName;
    private ImageView albumArt;
    private TextView trackName;
    private SeekBar seekBar;
    private TextView currentTime;
    private TextView trackLength;
    private Button backButton;
    private Button playPauseButton;
    private Button forwardButton;
    private Tracks tracks;
    private int defaultTrack = 0;
    private MediaPlayer mediaPlayer;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlayFragment() {
    }

    static PlayFragment newInstance(int defaultTrack, Tracks tracks) {
        PlayFragment newFragment = new PlayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DEFAULT_TRACK, defaultTrack);
        args.putSerializable(ARG_TRACKS, (Serializable)tracks);
        newFragment.setArguments(args);
        return newFragment;
    }

    public void setTracks(final Tracks tracks) {
        this.tracks = tracks;
        playTrack();
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        seekBar.setMax(mediaPlayer.getDuration());
        player.start();
    }

    public void playTrack() {
        try {
            String url = tracks.tracks.get(defaultTrack).preview_url; // your URL here
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
            // SHOW TOAST
            // CLOSE DIALOG
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_DEFAULT_TRACK)) {
            defaultTrack = getArguments().getInt(ARG_DEFAULT_TRACK);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.player, container, false);

        artistName = (TextView)contentView.findViewById(R.id.artist_name);
        albumName = (TextView)contentView.findViewById(R.id.album_name);
        albumArt = (ImageView)contentView.findViewById(R.id.album_art);
        trackName = (TextView)contentView.findViewById(R.id.track_name);
        seekBar = (SeekBar)contentView.findViewById(R.id.seek_bar);
        currentTime = (TextView)contentView.findViewById(R.id.current_time);
        trackLength = (TextView)contentView.findViewById(R.id.track_length);
        backButton = (Button)contentView.findViewById(R.id.back_button);
        playPauseButton = (Button)contentView.findViewById(R.id.play_pause_button);
        forwardButton = (Button)contentView.findViewById(R.id.forward_button);

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer == null) {
                    playTrack();
                } else {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    } else {
                        mediaPlayer.pause();
                    }
                }

            }
        });

        return contentView;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    private Handler mHandler = new Handler();
    //Make sure you update Seekbar on UI thread

//    PlayFragment.this.getActivity().runOnUiThread(new Runnable() {
//
//        @Override
//        public void run() {
//            if(mediaPlayer != null){
//                int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
//                seekBar.setProgress(mCurrentPosition);
//            }
//            mHandler.postDelayed(this, 1000);
//        }
//    });
}
