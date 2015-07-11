package com.christopherpick.anroid.spotifystreamer.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.christopherpick.anroid.spotifystreamer.R;
import com.christopherpick.anroid.spotifystreamer.activities.ArtistDetailActivity;
import com.christopherpick.anroid.spotifystreamer.activities.ArtistListActivity;
import com.christopherpick.anroid.spotifystreamer.adapters.TrackAdapter;
import com.christopherpick.anroid.spotifystreamer.utils.ShowToastMessage;
import com.christopherpick.anroid.spotifystreamer.helpers.SpotifyHelper;
import kaaes.spotify.webapi.android.models.Tracks;

import java.util.HashMap;
import java.util.Map;

/**
 * A fragment representing a single Artist detail screen.
 * This fragment is either contained in a {@link ArtistListActivity}
 * in two-pane mode (on tablets) or a {@link ArtistDetailActivity}
 * on handsets.
 */
public class ArtistDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ARTIST_NAME = "artist_name";
    public static final Map<String, Object> ARG_MAP = new HashMap<>();


    private TrackAdapter mTrackAdapter;
    private String artistName;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_detail, container, false);

        ListView artistList = (ListView) rootView.findViewById(R.id.artist_detail_list);
        mTrackAdapter = new TrackAdapter(getActivity(), R.layout.track_row);
        artistList.setAdapter(mTrackAdapter);
        ARG_MAP.put("country", getActivity().getResources().getConfiguration().locale.getCountry());

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            new FetchArtistsTask().execute(getArguments().getString(ARG_ITEM_ID));
        }
        if (getArguments().containsKey(ARG_ARTIST_NAME)) {
            artistName = getArguments().getString(ARG_ARTIST_NAME);
        }


        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void setTitle(String title);
        public void setSubTitle(String subTitle);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void setTitle(String title) {

        }

        @Override
        public void setSubTitle(String subTitle) {

        }
    };

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    private class FetchArtistsTask extends AsyncTask<String, Integer, Tracks> {

        @Override
        protected Tracks doInBackground(String... params) {
            int count = params.length;
            if (count == 1) {
                return SpotifyHelper.getInstance().getSpotifyServiceInstancce().getArtistTopTrack(params[0], ARG_MAP);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Tracks tracks) {
            mTrackAdapter.clear();
            if (tracks.tracks.size() > 0) {
                mTrackAdapter.addAll(tracks.tracks);
                mCallbacks.setTitle(String.format(getString(R.string.top_tracks), tracks.tracks.size()));
                mCallbacks.setSubTitle(artistName);
            } else {
                ShowToastMessage.showToast(getActivity(), R.string.sorry_no_tracks);
            }
        }


    }
}
