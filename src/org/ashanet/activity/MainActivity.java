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

public class MainActivity
    extends Activity
    implements ProgressIndicator,
               ActionBar.TabListener 
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
        actionBar.addTab(actionBar.newTab()
                         .setText(R.string.tab_projects)
                         .setTag("projects")
                         .setTabListener(this));
        actionBar.addTab(actionBar.newTab()
                         .setText(R.string.tab_events)
                         .setTag("events")
                         .setTabListener(this));
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
    //Called when a tab that is already selected is chosen again by the user.
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        Log.d("DEBUG", "Tab reselected: " + tab.getTag());
        Toast.makeText(this, "You're looking at that already.",
                       Toast.LENGTH_SHORT).show();
    }

    //Called when a tab enters the selected state.
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        Log.d("DEBUG", "Tab selected: " + tab.getTag());
        if (tab.getTag() == "projects") {
            if (projectsFragment == null)
                createProjectsFragment();
            ft.add(R.id.flMain, projectsFragment, "projects");
        }
        else {
            if (eventsFragment == null)
                createEventsFragment();
            Log.d("DEBUG", "Replacing flMain with " + eventsFragment);
            ft.add(R.id.flMain, eventsFragment, "events");
        }
    }

    //Called when a tab exits the selected state.
    public void	onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        Log.d("DEBUG", "Tab unselected: " + tab.getTag());
        ft.detach(currentFragment);
    }
}
