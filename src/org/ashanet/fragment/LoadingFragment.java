
package org.ashanet.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.List;
import org.ashanet.R;
import org.ashanet.AshaNetApp;
import org.ashanet.interfaces.FragmentNavigation;
import org.ashanet.interfaces.ProgressIndicator;
import org.ashanet.util.TypeMaps;

public class LoadingFragment extends Fragment {
    private ProgressIndicator pi;
    private FragmentNavigation fn;
    private ArrayAdapter<String> adpt;
    private TypeMaps tm;

    // FIXME - this should go in a base class.
    public LoadingFragment(ProgressIndicator pi, FragmentNavigation fn,
                           TypeMaps typeMaps)
    {
        this.pi = pi;
        this.fn = fn;
        this.tm = typeMaps;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d("DEBUG", "Inflating LoadingFragment");
        View v = inflater.inflate
            (R.layout.fragment_loading, container, false);
        Log.d("DEBUG", "Returning LoadingFragment");
        adpt = new ArrayAdapter<String>
            (getActivity(), R.layout.item_load_message);
        ((ListView) v.findViewById(R.id.lvLoadingMessages))
            .setAdapter(adpt);
        loadStuff();
        Log.d("DEBUG", "LoadingFragment.onCreate finished");
        return v;
    }

    enum STUFF { CHAPTER, FOCUSTYPE, PROJECTTYPE, STATE, STATUS };
    private STUFF got = null;

    public void loadStuff() {
        // FIXME: the enum could have this info
        String table = ((got == null) ? "Chapter" :
                        (got == STUFF.CHAPTER) ? "FocusType" :
                        (got == STUFF.FOCUSTYPE) ? "ProjectType" :
                        (got == STUFF.PROJECTTYPE) ? "State" :
                        (got == STUFF.STATE) ? "Status" :
                        null);

        if (table == null) {
            fn.popFragment(R.string.title_loading);
        }
        else {
            adpt.add("Loading " + table + "...");
            ParseQuery<ParseObject> query = ParseQuery.getQuery(table);
            query.findInBackground
                (new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> thingList,
                                     ParseException e) {
                        if (e != null) {
                            Toast.makeText(getActivity(), "Failed! " + e,
                                           Toast.LENGTH_LONG).show();
                        }
                        else {
                            saveStuff(thingList);
                            loadStuff();
                        }
                    }
                });
        }
    }

    private void saveStuff(List<ParseObject> thingList) {
        if (got == null)
            got = got.values()[0];
        else
            got = got.values()[got.ordinal() + 1];

        Log.d("DEBUG", "Saving " + got + " from " + thingList + " into " + tm);
        switch (got) {
        case CHAPTER:
            tm.setChapters(thingList);
            break;
        case FOCUSTYPE:
            tm.setFocusTypes(thingList);
            break;
        case PROJECTTYPE:
            tm.setProjectTypes(thingList);
            break;
        case STATE:
            tm.setStates(thingList);
            break;
        case STATUS:
            tm.setStatuses(thingList);
            break;
        }
    }
}

