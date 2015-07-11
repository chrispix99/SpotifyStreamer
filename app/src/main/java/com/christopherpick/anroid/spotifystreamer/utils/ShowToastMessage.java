package com.christopherpick.anroid.spotifystreamer.utils;

import android.content.Context;
import android.widget.Toast;
import com.christopherpick.anroid.spotifystreamer.R;

/**
 * Shows a toast message, cancels the previous one.
 */
public class ShowToastMessage {
    private static Toast noArtistToast;

    /**
     * Show a Toast message that by default cancels any previous toasts.
     * defaults to Toast.LENGTH_LONG
     * @param context       context
     * @param resourceId    string resource to display
     */
    public static void showToast(Context context, int resourceId) {
       showToast(context, resourceId, Toast.LENGTH_LONG, true);
    }

    /**
     * Show a Toast message that by default cancels any previous toasts.
     * @param context       context
     * @param resourceId    string resource to display
     * @param length        toast length (time to display)
     */
    public static void showToast(Context context, int resourceId, int length) {
        showToast(context, resourceId, length, true);
    }

    /**
     * Show a Toast message that can specify to cancel the previous toast.
     * Defaults to Toast.LENGTH_LONG
     * @param context           context
     * @param resourceId        string resource to display
     * @param cancelPrevious    cancel the previous toast
     */
    public static void showToast(Context context, int resourceId, boolean cancelPrevious) {
        showToast(context, resourceId, Toast.LENGTH_LONG, cancelPrevious);
    }

    /**
     * Show a toast message that can specify both the length and if it is cancelable.
     * @param context           context
     * @param resourceId        string resource to display
     * @param length            toast length (time to display)
     * @param cancelPrevious    cancel the previous toast
     */
    public static void showToast(Context context, int resourceId, int length, boolean cancelPrevious) {
        if (noArtistToast != null && cancelPrevious) {
            noArtistToast.cancel();
        }
        noArtistToast = Toast.makeText(context, resourceId, length);
        noArtistToast.show();
    }
}
