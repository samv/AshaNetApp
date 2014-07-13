package org.ashanet.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import org.ashanet.R;
import org.ashanet.adapter.NavDrawerAdapter;
import org.ashanet.fragment.EventListFragment;
import org.ashanet.fragment.ProjectListFragment;
import org.ashanet.interfaces.ProgressIndicator;
import org.ashanet.util.FragmentTabListener;

public class MainActivity
    extends ActionBarActivity
    implements ProgressIndicator,
               ActionBar.OnNavigationListener,
               NavDrawerAdapter.Callbacks
{
    ProjectListFragment projectsFragment;
    EventListFragment eventsFragment;
    Fragment currentFragment;
    NavDrawerAdapter nav;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        setProgressBarIndeterminateVisibility(false);

        Log.d("DEBUG", "ActionBar!");
        // TODO ActionBar should have "verbs" for current fragment
        //ActionBar actionBar = getActionBarSupport();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        //SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource
            //(this, R.array.project_verbs,
             //android.R.layout.simple_spinner_dropdown_item);
        //actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);

        Log.d("DEBUG", "Nav Drawer!");
        setupNavDrawer();
        Log.d("DEBUG", "Nav Drawer Done!");
    }

    private void setupNavDrawer() {
        // Set the adapter for the list view
        Log.d("DEBUG", "Nav Drawer Adapter!");
        nav = new NavDrawerAdapter(this, this);
        Log.d("DEBUG", "Navbar ListView");
        ListView lvNavDrawer = (ListView) findViewById(R.id.lvNavDrawer);
        lvNavDrawer.setAdapter(nav);
        lvNavDrawer.setOnItemClickListener(nav);
        mTitle = mDrawerTitle = getTitle();

        Log.d("DEBUG", "mDrawer!");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle
            (this, mDrawerLayout, R.drawable.ic_drawer,
             R.string.drawer_open, R.string.drawer_close)
        {
            /* Called when a drawer has settled in a completely closed
             * state. */
            public void onDrawerClosed(View view) {
                Log.d("DEBUG", "Drawer Closed");
               super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
                // creates call to onPrepareOptionsMenu()
                invalidateOptionsMenu();
            }

            /* Called when a drawer has settled in a completely open
             * state. */
            public void onDrawerOpened(View drawerView) {
                Log.d("DEBUG", "Drawer Opened");
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
                // creates call to onPrepareOptionsMenu()
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        Log.d("DEBUG", "mDrawer listener!");
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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

    public void onNounSelected(NavDrawerAdapter.Noun noun) {
        Log.d("DEBUG", "Noun selected: " + noun);
    }
    
    public void onGlobalAction(NavDrawerAdapter.GlobalAction action) {
        Log.d("DEBUG", "Action selected: " + action);
    }
}
