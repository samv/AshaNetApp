package org.ashanet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

public class ListProjectsActivity extends Activity
{
    private ParseQueryAdapter<ParseObject> adapter;
    private ListView lvProjects;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_projects);
        Log.d("DEBUG", "Creating Parse Query Adapter");
        adapter = new ParseQueryAdapter<ParseObject>
            (this, "Project", android.R.layout.simple_list_item_1);
        Log.d("DEBUG", "Setting text key");
        adapter.setTextKey("name");
 
        lvProjects = (ListView) findViewById(R.id.lvProjects);
        Log.d("DEBUG", "Connecting adapter");
        lvProjects.setAdapter(adapter);
        Log.d("DEBUG", "done");
    }
}
