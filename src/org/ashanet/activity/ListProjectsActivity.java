package org.ashanet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import org.ashanet.R;
import org.ashanet.adapter.ProjectListAdapter;
import org.ashanet.typedef.Project;

public class ListProjectsActivity
    extends Activity
    implements AdapterView.OnItemClickListener
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
}


