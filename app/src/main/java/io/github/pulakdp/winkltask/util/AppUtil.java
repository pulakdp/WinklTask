package io.github.pulakdp.winkltask.util;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.view.View;

public class AppUtil {

    public static int getBlackOrWhiteColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness >= 0.5) {
            return Color.WHITE;
        } else return Color.BLACK;
    }

    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE));

        return connectivityManager != null &&
                connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @BindingAdapter("booleanVisibility")
    public static void bindVisibility(View view, Boolean val) {
        if (val == null || !val) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }
}
