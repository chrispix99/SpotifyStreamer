package com.christopherpick.anroid.spotifystreamer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.christopherpick.anroid.spotifystreamer.R;
import com.christopherpick.anroid.spotifystreamer.utils.ImageUtils;
import com.squareup.picasso.Picasso;
import kaaes.spotify.webapi.android.models.Artist;

/**
 * Adapter that shows the artist list from a search result.
 */
public class ArtistAdapter extends ArrayAdapter<Artist> {

    // private List<Artist> artistList;
    private final Context context;
    private final int resourceId;

    public ArtistAdapter(Context context, int resource) {
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
            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);
        }

        Holder viewHolder = (Holder) convertView.getTag();
        Artist artist = getItem(position);

        viewHolder.textView.setText(artist.name);
        String bestImageUrl = ImageUtils.getBestImageUrl(artist.images, 200);
        if (bestImageUrl != null && URLUtil.isValidUrl(bestImageUrl)) {
            Picasso.with(context).load(bestImageUrl).into(viewHolder.imageView);
        }
        return convertView;
    }

    public class Holder {
        public TextView textView;
        public ImageView imageView;
    }
}
