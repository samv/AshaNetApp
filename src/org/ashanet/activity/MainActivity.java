package org.ashanet.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.FrameLayout;
import org.ashanet.R;
import org.ashanet.fragment.ProjectListFragment;
import org.ashanet.interfaces.ProgressIndicator;

public class MainActivity
    extends Activity
    implements ProgressIndicator
{
    ProjectListFragment projectsFragment;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        setProgressBarIndeterminateVisibility(false);
        createProjectsFragment();
    }

    public void createProjectsFragment() {
        projectsFragment = new ProjectListFragment(this);
        chooseFragment(projectsFragment);
    }

    void chooseFragment(Fragment frag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.flMain, frag);
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
