package com.christopherpick.anroid.spotifystreamer.fragments;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.christopherpick.anroid.spotifystreamer.R;
import com.christopherpick.anroid.spotifystreamer.services.MediaService;
import com.christopherpick.anroid.spotifystreamer.utils.ShowToastMessage;
import com.squareup.picasso.Picasso;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by chrispix on 7/11/15.
 */
public class PlayFragment extends DialogFragment implements View.OnClickListener, MediaService.IMediaServiceCallbacks, SeekBar.OnSeekBarChangeListener {

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

    private MediaService mService;
    private boolean mBound = false;


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaService.LocalBinder binder = (MediaService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            if (getArguments() != null) {
                mService.registerCallback(PlayFragment.this);
                if (getArguments().containsKey(ARG_TRACKS) && getArguments().containsKey(ARG_DEFAULT_TRACK)) {
                    mService.setTracks((Tracks)getArguments().getSerializable(ARG_TRACKS), getArguments().getInt(ARG_DEFAULT_TRACK));
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }

    };

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.player, container, false);

        artistName = (TextView) contentView.findViewById(R.id.artist_name);
        albumName = (TextView) contentView.findViewById(R.id.album_name);
        albumArt = (ImageView) contentView.findViewById(R.id.album_art);
        trackName = (TextView) contentView.findViewById(R.id.track_name);
        seekBar = (SeekBar) contentView.findViewById(R.id.seek_bar);
        currentTime = (TextView) contentView.findViewById(R.id.current_time);
        trackLength = (TextView) contentView.findViewById(R.id.track_length);

        backButton = (Button) contentView.findViewById(R.id.back_button);
        playPauseButton = (Button) contentView.findViewById(R.id.play_pause_button);
        forwardButton = (Button) contentView.findViewById(R.id.forward_button);

        playPauseButton.setOnClickListener(this);
        forwardButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);


        return contentView;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), MediaService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unbind from the service
        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }

//    /**
//     * This sets up the async method to start playing the track.
//     */
//    private void playTrack() {
//        if (mService != null) {
//            mService.resumeTrack();
//            setViewFromTrack();
//        } else {
//            ShowToastMessage.showToast(getActivity(), R.string.error_playing_track);
//        }
//    }

    private void playPauseClick() {
        if (mService != null) {
            if (mService.isPaused() || !mService.isPlaying()) {
                playPauseButton.setEnabled(false);
                mService.resumeTrack();
            } else {
                playPauseButton.setEnabled(false);
                mService.pauseTrack();
            }
        }
    }

    /**
     * Sets the current view from the track information in the service about the
     * currently selected track.
     */
    private void setViewFromTrack() {
        clearTrackData();
        if (mService != null) {
            artistName.setText(mService.getArtistName());
            albumName.setText(mService.getAlbumName());
            if (getActivity() != null) {
                Picasso.with(getActivity()).load(mService.getFullSizeImageUrl()).into(albumArt);
            }
            trackName.setText(mService.getTrackName());
            seekBar.setProgress(0);
            currentTime.setText("0");
            trackLength.setText(mService.getTrackLength());
        } else {
            ShowToastMessage.showToast(getActivity(), R.string.media_service_stopped);
        }
    }

    /**
     * Clears all the UI Components in the event that we can't populate all the fields,
     * we don't want dirty data showing
     */
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
    public SeekBar getSeekBar() {
        return seekBar;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_pause_button: {
                playPauseClick();
                break;
            }
            case R.id.back_button: {
                if (mService != null) {
                    forwardButton.setEnabled(false);
                    backButton.setEnabled(false);
                    mService.backTrack();
                }
                break;
            }
            case R.id.forward_button: {
                if (mService != null) {
                    forwardButton.setEnabled(false);
                    backButton.setEnabled(false);
                    mService.forwardTrack();
                }
                break;
            }
        }
    }

    @Override
    public void mediaChanged() {
        setViewFromTrack();
        forwardButton.setEnabled(true);
        backButton.setEnabled(true);
    }

    @Override
    public void currentTime(String time) {
        currentTime.setText(time);
    }

    @Override
    public void nowPlaying() {
        if (playPauseButton != null) {
            playPauseButton.setEnabled(true);
            playPauseButton.setBackgroundResource(android.R.drawable.ic_media_pause);
        }
    }

    @Override
    public void nowPaused() {
        if (playPauseButton != null) {
            playPauseButton.setEnabled(true);
            playPauseButton.setBackgroundResource(android.R.drawable.ic_media_play);
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (mService != null) {
                mService.setCurrentPosition(seekBar.getProgress());
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
