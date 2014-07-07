package org.ashanet;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;
import com.parse.Parse;
import com.parse.ParseObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import org.ashanet.typedef.Project;

public class AshaNetApp extends Application {

    public String parseAppId;

    @Override
    public void onCreate() {
        super.onCreate();
        LineNumberReader lnr = new LineNumberReader
            (new InputStreamReader
             (getResources().openRawResource(R.raw.keys)));

        registerClasses();

        String parseClientKey;

        try {
            parseAppId = lnr.readLine();
            parseClientKey = lnr.readLine();


            Parse.initialize(this, parseAppId, parseClientKey);
            Log.d("DEBUG", "Successfully initialized");
        }
        catch (IOException ioe) {
            Log.e("DEBUG", "Failed to read keys", ioe);
        }
    }

    private void registerClasses() {
        ParseObject.registerSubclass(Project.class);
    }
}
