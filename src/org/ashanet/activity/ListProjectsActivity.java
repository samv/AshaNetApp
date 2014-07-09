package org.ashanet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import org.ashanet.R;
import org.ashanet.adapter.ProjectListAdapter;
import org.ashanet.interfaces.ProgressIndicator;
import org.ashanet.typedef.Project;

public class ListProjectsActivity
    extends Activity
    implements AdapterView.OnItemClickListener,
               ProgressIndicator
{
    public ProjectListAdapter pla;
    private ListView lvProjects;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_list_projects);
        setProgressBarIndeterminateVisibility(false);
        Log.d("DEBUG", "Creating Parse Query Adapter");
        pla = new ProjectListAdapter(this, this);
        Log.d("DEBUG", "Setting text key");
        pla.setTextKey("name");
 
        lvProjects = (ListView) findViewById(R.id.lvProjects);
        Log.d("DEBUG", "Connecting adapter to " + lvProjects);
        lvProjects.setAdapter(pla);
        lvProjects.setOnItemClickListener(this);
        Log.d("DEBUG", "set on item click listener");
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id)   
    {
        Log.d("DEBUG", "clicked on item " + position);
        Intent i = new Intent(this, ProjectDetailsActivity.class);
        Project p = pla.getItem(position);
        Log.d("DEBUG", "putting project = " + p.getObjectId());
        i.putExtra("projectId", p.getObjectId());
        startActivity(i);
    }

    @Override
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


