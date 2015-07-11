package com.christopherpick.anroid.spotifystreamer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.christopherpick.anroid.spotifystreamer.R;
import com.christopherpick.anroid.spotifystreamer.utils.ImageUtils;
import com.squareup.picasso.Picasso;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Adapter that shows a list of tracks for a particular Artist.
 */
public class TrackAdapter extends ArrayAdapter<Track> {

    private final Context context;
    private final int resourceId;

    public TrackAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, parent, false);
            Holder viewHolder = new Holder();
            viewHolder.album = (TextView) convertView.findViewById(R.id.album);
            viewHolder.track = (TextView) convertView.findViewById(R.id.track);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);
        }

        Holder viewHolder = (Holder) convertView.getTag();
        Track track = getItem(position);
        viewHolder.album.setText(track.album.name);
        viewHolder.track.setText(track.name);
        Picasso.with(context).load(ImageUtils.getBestImageUrl(track.album.images, 200)).into(viewHolder.imageView);

        return convertView;
    }

    public class Holder {
        public TextView album;
        public TextView track;
        public ImageView imageView;
    }
}