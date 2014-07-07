package org.ashanet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import org.ashanet.adapter.ProjectListAdapter;

public class ListProjectsActivity extends Activity
{
    public ProjectListAdapter pla;
    private ListView lvProjects;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_projects);
        Log.d("DEBUG", "Creating Parse Query Adapter");
        pla = new ProjectListAdapter(this);
        Log.d("DEBUG", "Setting text key");
        pla.setTextKey("name");
 
        lvProjects = (ListView) findViewById(R.id.lvProjects);
        Log.d("DEBUG", "Connecting adapter");
        lvProjects.setAdapter(pla);
        Log.d("DEBUG", "done");
    }
}
