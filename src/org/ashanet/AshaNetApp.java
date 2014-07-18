package org.ashanet;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;
import com.parse.Parse;
import com.parse.ParseObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import org.ashanet.typedef.Chapter;
import org.ashanet.typedef.Event;
import org.ashanet.typedef.FocusType;
import org.ashanet.typedef.Project;
import org.ashanet.typedef.ProjectType;
import org.ashanet.typedef.State;
import org.ashanet.typedef.Status;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class AshaNetApp extends Application {

    public String parseAppId;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault
            ("fonts/Gotham-Light.ttf", R.attr.fontPath);
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
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Chapter.class);
        ParseObject.registerSubclass(FocusType.class);
        ParseObject.registerSubclass(ProjectType.class);
        ParseObject.registerSubclass(State.class);
        ParseObject.registerSubclass(Status.class);
    }
}
