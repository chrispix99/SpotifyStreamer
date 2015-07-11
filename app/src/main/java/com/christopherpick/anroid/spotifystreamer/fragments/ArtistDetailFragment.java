package com.christopherpick.anroid.spotifystreamer.fragments;

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
    public static final Map<String, Object> ARG_MAP = new HashMap<>();


    private TrackAdapter mTrackAdapter;

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


        return rootView;
    }

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
            } else {
                ShowToastMessage.showToast(getActivity(), R.string.sorry_no_tracks);
            }
        }
    }
}
