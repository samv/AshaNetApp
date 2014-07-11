package org.ashanet.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import org.ashanet.R;
import org.ashanet.fragment.EventListFragment;
import org.ashanet.fragment.ProjectListFragment;
import org.ashanet.interfaces.ProgressIndicator;
import org.ashanet.util.FragmentTabListener;

public class MainActivity
    extends Activity
    implements ProgressIndicator,
               ActionBar.OnNavigationListener
{
    ProjectListFragment projectsFragment;
    EventListFragment eventsFragment;
    Fragment currentFragment;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        setProgressBarIndeterminateVisibility(false);
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource
            (this, R.array.action_list,
             android.R.layout.simple_spinner_dropdown_item);
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
    }

    public Fragment getProjectsFragment() {
        Log.d("DEBUG", "Creating Projects Fragment");
        if (projectsFragment == null)
            projectsFragment = new ProjectListFragment(this);
        return projectsFragment;
    }

    public Fragment getEventsFragment() {
        Log.d("DEBUG", "Creating Events Fragment");
        if (eventsFragment == null)
            eventsFragment = new EventListFragment(this);
        return eventsFragment;
    }

    void chooseFragment(Fragment frag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.flMain, frag);
        currentFragment = frag;
        ft.commit();
    }

    public void setProgressIndicator(int det8, boolean indet8) {
        if (indet8 || ((det8 >= 0) && det8 <= 10000)) {
            Log.d("DEBUG", "Something's happening!");
            setProgressBarIndeterminateVisibility(true);
        }
        else {
            Log.d("DEBUG", "All done!");
            setProgressBarIndeterminateVisibility(false);
        }
    }
    
    @Override
    public boolean onNavigationItemSelected(int position, long itemId) {
        switch (position) {
        case 0:
            chooseFragment(getProjectsFragment());
            break;
        case 1:
            chooseFragment(getEventsFragment());
            break;
        default:
            Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
            break;
        }
        return true;
    }
}
