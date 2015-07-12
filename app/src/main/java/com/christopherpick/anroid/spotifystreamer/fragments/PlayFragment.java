package com.christopherpick.anroid.spotifystreamer.fragments;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.*;
import com.christopherpick.anroid.spotifystreamer.R;
import com.christopherpick.anroid.spotifystreamer.utils.ImageUtils;
import com.christopherpick.anroid.spotifystreamer.utils.ShowToastMessage;
import com.squareup.picasso.Picasso;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

import java.io.IOException;

/**
 * Created by chrispix on 7/11/15.
 */
public class PlayFragment extends DialogFragment implements View.OnClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

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
    private int currentTrack = 0;
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
        args.putSerializable(ARG_TRACKS, tracks);
        newFragment.setArguments(args);
        return newFragment;
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        seekBar.setMax(mediaPlayer.getDuration());
        startPlaying();
    }

    /**
     * This sets up the async method to start playing the track.
     */
    private void playTrack() {
        try {
            setViewFromTrack();
            String url = tracks.tracks.get(currentTrack).preview_url; // your URL here
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
            ShowToastMessage.showToast(getActivity(), R.string.error_playing_track);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_DEFAULT_TRACK)) {
                currentTrack = getArguments().getInt(ARG_DEFAULT_TRACK);
            }
            if (getArguments().containsKey(ARG_TRACKS)) {
                tracks = (Tracks)getArguments().getSerializable(ARG_TRACKS);
            }
        }
    }

    /**
     * Starts playing the currently queued track again, if no media player has been configured, it will
     * start playing the track.
     */
    private void startPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            playPauseButton.setBackgroundResource(android.R.drawable.ic_media_pause);
        } else {
            playTrack();
        }
    }

    private void pausePlaying() {
        mediaPlayer.pause();
        playPauseButton.setBackgroundResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
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

        playPauseButton.setOnClickListener(this);
        forwardButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        playTrack();

        return contentView;
    }

    private void setViewFromTrack() {
        clearTrackData();
        Track track = tracks.tracks.get(currentTrack);
        artistName.setText(track.artists.get(0).name);
        albumName.setText(track.album.name);
        String bestImageUrl = ImageUtils.getBestImageUrl(track.album.images, 600);
        if (getActivity() != null) {
            Picasso.with(getActivity()).load(bestImageUrl).into(albumArt);
        }
        trackName.setText(track.name);
        seekBar.setProgress(0);
        currentTime.setText("0");
        trackLength.setText(String.valueOf(track.duration_ms*0.001) + "s");
    }

    private void clearTrackData() {
        artistName.setText("");
        albumName.setText("");
        if (getActivity() != null) {
            Picasso.with(getActivity()).load((String) null).into(albumArt);
        }
        trackName.setText("");
        seekBar.setProgress(0);
        currentTime.setText("");
        trackLength.setText("");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        forwardTrack();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    private Handler mHandler = new Handler();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_pause_button: {
                playPauseClick();
                break;
            }
            case R.id.back_button: {
                backTrack();
                break;
            }
            case R.id.forward_button: {
                forwardTrack();
                break;
            }
        }
    }

    private void forwardTrack() {
        currentTrack += 1;
        if (currentTrack >= tracks.tracks.size()) {
            currentTrack = 0;
        }
        trackChange();
    }

    private void backTrack() {
        currentTrack -= 1;
        if (currentTrack < 0) {
            currentTrack = tracks.tracks.size()-1;
        }
        trackChange();
    }

    private void trackChange() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        playTrack();
    }

    private void playPauseClick() {
        if (mediaPlayer == null) {
            playTrack();
        } else {
            if (!mediaPlayer.isPlaying()) {
                startPlaying();
            } else {
                pausePlaying();
            }
        }
    }
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
