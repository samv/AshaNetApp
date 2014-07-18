package org.ashanet;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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
import com.parse.ParseUser;
import java.util.ArrayList;
import org.ashanet.adapter.NavDrawerAdapter;
import org.ashanet.fragment.AboutFragment;
import org.ashanet.fragment.EventListFragment;
import org.ashanet.fragment.LoadingFragment;
import org.ashanet.fragment.LoginFragment;
import org.ashanet.fragment.ProjectListFragment;
import org.ashanet.interfaces.FragmentNavigation;
import org.ashanet.interfaces.ProgressIndicator;
import org.ashanet.util.FragmentTabListener;
import org.ashanet.util.TypeMaps;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity
    extends ActionBarActivity
    implements ProgressIndicator,
               FragmentNavigation,
               ActionBar.OnNavigationListener,
               NavDrawerAdapter.Callbacks,
               FragmentManager.OnBackStackChangedListener
{
    ProjectListFragment projectsFragment;
    EventListFragment eventsFragment;
    LoadingFragment loadingFragment;
    Fragment currentFragment;
    NavDrawerAdapter nav;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ListView lvNavDrawer;
    private ActionBar actionBar;
    public TypeMaps typeMaps;
    private ArrayList<String> fragTitles;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

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
        actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        //SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource
            //(this, R.array.project_verbs,
             //android.R.layout.simple_spinner_dropdown_item);
        //actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);

        fragTitles = new ArrayList<String>();
        Log.d("DEBUG", "Nav Drawer!");
        setupNavDrawer();
        Log.d("DEBUG", "Nav Drawer Done!");
        typeMaps = new TypeMaps();
        chooseFragment(getLoadingFragment(), 0);
    }

    private void setupNavDrawer() {
        // Set the adapter for the list view
        Log.d("DEBUG", "Nav Drawer Adapter!");
        nav = new NavDrawerAdapter(this, this);
        Log.d("DEBUG", "Navbar ListView");
        lvNavDrawer = (ListView) findViewById(R.id.lvNavDrawer);
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
                actionBar.setTitle(mTitle);
                // creates call to onPrepareOptionsMenu()
                invalidateOptionsMenu();
            }

            /* Called when a drawer has settled in a completely open
             * state. */
            public void onDrawerOpened(View drawerView) {
                Log.d("DEBUG", "Drawer Opened");
                super.onDrawerOpened(drawerView);
                actionBar.setTitle(mDrawerTitle);
                nav.setUser(ParseUser.getCurrentUser());
                // creates call to onPrepareOptionsMenu()
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        Log.d("DEBUG", "mDrawer listener!");
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        shouldDisplayHomeUp();
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
            projectsFragment = new ProjectListFragment(this, this, typeMaps);
        return projectsFragment;
    }

    public Fragment getEventsFragment() {
        Log.d("DEBUG", "Creating Events Fragment");
        if (eventsFragment == null)
            eventsFragment = new EventListFragment(this, this, typeMaps);
        return eventsFragment;
    }

    public Fragment getLoadingFragment() {
        Log.d("DEBUG", "Creating Loading Fragment");
        if (loadingFragment == null)
            loadingFragment = new LoadingFragment(this, this, typeMaps);
        return loadingFragment;
    }

    void chooseFragment(Fragment frag, int position) {
        FragmentTransaction ft = getSupportFragmentManager()
            .beginTransaction();
        ft.replace(R.id.flMain, frag);
        currentFragment = frag;
        ft.commit();
        lvNavDrawer.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(lvNavDrawer);
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
    
    public boolean onNavigationItemSelected(int position, long itemId) {
        Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp(){
        //Enable Up button only  if there are entries in the back stack
        FragmentManager fm = getSupportFragmentManager();
        boolean canback = fm.getBackStackEntryCount() > 0;
        mDrawerToggle.setDrawerIndicatorEnabled(!canback);
        Log.d("DEBUG", "Drawer Indicator is " + (!canback ? "ENABLED": "DISABLED"));
        actionBar.setTitle(canback ? fragTitles.get(fragTitles.size()-1) : mTitle);
    }

    public void pushFragment(Fragment newFrag, int titleResourceId,
                             String newFragTag)
    {
        Log.d("DEBUG", "push Fragment: " + newFrag);
        String fragTag = (newFragTag != null) ? newFragTag :
            getResources().getString(titleResourceId);
        fragTitles.add(fragTag);
        Log.d("DEBUG", "Making a new '" + fragTag + "'");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Log.d("DEBUG", "ft.replace()");
        ft.replace(R.id.flMain, newFrag);
        currentFragment = newFrag;
        Log.d("DEBUG", "ft.addToBackStack()");
        ft.addToBackStack(fragTag);
        Log.d("DEBUG", "ft.commit()");
        ft.commit();
    }

    public void popFragment(int titleResourceId) {
        Log.d("DEBUG", "popping fragment: " + titleResourceId);
        // FIXME: Fragment manager does actually let you pick out a
        // fragment by tag, perhaps this could keep popping until it
        // finds it.
        if (currentFragment == loadingFragment) {
            chooseFragment(getProjectsFragment(), 0);
        }
        else {
            getSupportFragmentManager().popBackStack();
            if (fragTitles.size() > 0)
                fragTitles.remove(fragTitles.size() - 1);
        }
    }

    public void onNounSelected(NavDrawerAdapter.Noun noun) {
        Log.d("DEBUG", "Noun selected: " + noun);
        switch (noun) {
        case PROJECTS:
            chooseFragment(getProjectsFragment(), nav.getItemId(noun));
            break;
        case EVENTS:
            chooseFragment(getEventsFragment(), nav.getItemId(noun));
            break;
        default:
            Toast.makeText(this, noun + " is TODO", Toast.LENGTH_SHORT).show();
            break;
        }
    }

    public void doLogout() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseUser.logOut();
        if (currentUser != null)
            Toast.makeText(this, "Logged out " + currentUser.getUsername(),
                           Toast.LENGTH_LONG).show();
        currentUser = ParseUser.getCurrentUser();
    }
    
    public void onGlobalAction(NavDrawerAdapter.GlobalAction action) {
        Log.d("DEBUG", "Action selected: " + action);
        switch (action) {
        case LOGOUT:
            doLogout();
            mDrawerLayout.closeDrawer(lvNavDrawer);
            break;
        case LOGIN:
            pushFragment
                (new LoginFragment(this, this), R.string.title_login, null);
            mDrawerLayout.closeDrawer(lvNavDrawer);
            break;
        case ABOUT:
            pushFragment(new AboutFragment(this), R.string.title_about, null);
            mDrawerLayout.closeDrawer(lvNavDrawer);
            break;
        default:
            Toast.makeText(this, action + " is TODO",
                           Toast.LENGTH_SHORT).show();
        }
   }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        Log.d("DEBUG", "Navigated up!");
        getSupportFragmentManager().popBackStack();
        return true;
    }
}
