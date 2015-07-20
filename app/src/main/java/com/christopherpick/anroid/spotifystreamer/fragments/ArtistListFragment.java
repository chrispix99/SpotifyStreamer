package com.christopherpick.anroid.spotifystreamer.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.christopherpick.anroid.spotifystreamer.R;
import com.christopherpick.anroid.spotifystreamer.SpotifyApplication;
import com.christopherpick.anroid.spotifystreamer.adapters.ArtistAdapter;
import com.christopherpick.anroid.spotifystreamer.utils.ShowToastMessage;
import com.christopherpick.anroid.spotifystreamer.helpers.SpotifyHelper;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * A list fragment representing a list of Artists. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ArtistDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ArtistListFragment extends Fragment implements TextWatcher {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id, String artistName) {
        }
    };
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;
    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private ListView mListView;
    private ArtistAdapter mArtistAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_artist_list, container, false);
        mListView = (ListView) contentView.findViewById(R.id.artist_list_view);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.setItemChecked(position, true);
                mCallbacks.onItemSelected(mArtistAdapter.getItem(position).id, mArtistAdapter.getItem(position).name);
            }
        });

        mArtistAdapter = new ArtistAdapter(getActivity(), R.layout.artist_row);
        mListView.setAdapter(mArtistAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ((EditText) contentView.findViewById(R.id.search_string)).addTextChangedListener(this);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        mListView.setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            mListView.setItemChecked(mActivatedPosition, false);
        } else {
            mListView.setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    // I know I could have used a SearchView, but I chose to use a regular edit text.
    @Override
    public void afterTextChanged(Editable s) {
        if (s != null && s.toString().length() > 0) {
            new FetchArtistsTask().execute(s.toString());
        } else {
            mArtistAdapter.clear();
        }
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
        public void onItemSelected(String id, String artistName);
    }

    private class FetchArtistsTask extends AsyncTask<String, Integer, ArtistsPager> {

        @Override
        protected ArtistsPager doInBackground(String... params) {
            int count = params.length;
            if (count == 1) {
                if (getActivity() != null) {
                    if (((SpotifyApplication) getActivity().getApplication()).isOnline()) {
                        return SpotifyHelper.getInstance().getSpotifyServiceInstancce().searchArtists(params[0]);
                    } else {
                        ShowToastMessage.showToast(getActivity(), R.string.offline_warning, Toast.LENGTH_LONG);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArtistsPager artistsPager) {
            mArtistAdapter.clear();
            if (artistsPager != null && artistsPager.artists != null && artistsPager.artists.items != null) {
                if (artistsPager.artists.items.size() > 0) {
                    mArtistAdapter.addAll(artistsPager.artists.items);
                } else {
                    ShowToastMessage.showToast(getActivity(), R.string.sorry_no_artists);
                }
            }  else {
                ShowToastMessage.showToast(getActivity(), R.string.offline_warning);
            }
        }

    }
}


