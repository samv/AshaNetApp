package org.ashanet.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;
import org.ashanet.R;
import org.ashanet.fragment.EventListFragment;
import org.ashanet.fragment.ProjectListFragment;
import org.ashanet.interfaces.ProgressIndicator;
import org.ashanet.util.FragmentTabListener;

public class MainActivity
    extends Activity
    implements ProgressIndicator
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
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.addTab
            (actionBar.newTab()
             .setText(R.string.tab_projects)
             .setTag("projects")
             .setTabListener
             (new FragmentTabListener<ProjectListFragment>
              (R.id.flMain, this, "projects", ProjectListFragment.class)));
        actionBar.addTab
            (actionBar.newTab()
             .setText(R.string.tab_events)
             .setTag("events")
             .setTabListener
             (new FragmentTabListener<EventListFragment>
              (R.id.flMain, this, "events", EventListFragment.class)));
  }

    public void createProjectsFragment() {
        Log.d("DEBUG", "Creating Projects Fragment");
        projectsFragment = new ProjectListFragment(this);
    }

    public void createEventsFragment() {
        Log.d("DEBUG", "Creating Events Fragment");
        eventsFragment = new EventListFragment(this);
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
}
