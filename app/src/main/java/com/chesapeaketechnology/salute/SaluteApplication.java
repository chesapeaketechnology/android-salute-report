package com.chesapeaketechnology.salute;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;

public class SaluteApplication extends Application
{
    private static final String LOG_TAG = SaluteApplication.class.getSimpleName();

    @Override
    public void onCreate()
    {
        super.onCreate();

        // TODO: This can be safely deleted once this Google Maps SDK bug is fully resolved:
        // https://issuetracker.google.com/issues/154855417#comment457
        try {
            SharedPreferences hasFixedGoogleBug154855417 = getSharedPreferences("google_bug_154855417", Context.MODE_PRIVATE);
            if (!hasFixedGoogleBug154855417.contains("fixed")) {
                File corruptedZoomTables = new File(getFilesDir(), "ZoomTables.data");
                File corruptedSavedClientParameters = new File(getFilesDir(), "SavedClientParameters.data.cs");
                File corruptedClientParametersData =
                        new File(
                                getFilesDir(),
                                "DATA_ServerControlledParametersManager.data."
                                        + getBaseContext().getPackageName());
                File corruptedClientParametersDataV1 =
                        new File(
                                getFilesDir(),
                                "DATA_ServerControlledParametersManager.data.v1."
                                        + getBaseContext().getPackageName());
                corruptedZoomTables.delete();
                corruptedSavedClientParameters.delete();
                corruptedClientParametersData.delete();
                corruptedClientParametersDataV1.delete();
                hasFixedGoogleBug154855417.edit().putBoolean("fixed", true).apply();
            }
        } catch (Exception e) {
            Log.wtf(LOG_TAG, "Google Maps SDK bug workaround failed", e);
        }
    }
}
