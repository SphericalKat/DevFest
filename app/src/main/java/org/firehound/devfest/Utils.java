package org.firehound.devfest;

import android.app.Activity;
import android.widget.Toast;

public class Utils{
    private static final String TAG = "Utils";

    public static void toastWrapper(Activity activity, final String msg, final int length) {
        activity.runOnUiThread(() -> Toast.makeText(activity, msg, length).show());
    }
}
