package com.christopherpick.anroid.spotifystreamer.utils;

import kaaes.spotify.webapi.android.models.Image;

import java.util.List;

/**
 * Utilities to manipulate and select the correct image to display
 */
public class ImageUtils {

    /**
     * Selects the best image url for the partiular target size, really this should be done
     * and saved off based on our requirements vs at real time by decorating the class, vs
     * iterating over it for each view. To try and not cause a larger performance impact,
     * we are just iterating over the list without an iterator. From the documentation, it
     * appears that the list size is small and sorted largest to smallest, which is not always
     * guaranteed. We could break out of the list when we go past the smallest match, but
     * if the list is not ordered correctly we would possibly get nothing
     * TODO: look at image size and density to see if we can find a better match.
     * @param images        list of images
     * @param targetSize    target size in pixels
     * @return
     */
    public static String getBestImageUrl(List<Image> images, int targetSize) {
        String imageUrl;
        if (images.size() > 0) {
            // choose the first image as a default image, but still check it.
            imageUrl = images.get(0).url;
            for (int i = 0; i < images.size(); i++) {
                if (images.get(i).width >= targetSize) {
                    imageUrl = images.get(i).url;
                }
            }
        } else {
            imageUrl = null;
        }
        return imageUrl;
    }

}
