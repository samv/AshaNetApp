package org.ashanet.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import org.ashanet.R;
import org.ashanet.activity.ProjectDetailsActivity;
import org.ashanet.adapter.ProjectListAdapter;
import org.ashanet.interfaces.ProgressIndicator;
import org.ashanet.typedef.Project;

public class ProjectListFragment
    extends Fragment
    implements AdapterView.OnItemClickListener
{
    public ProjectListAdapter pla;
    private ListView lvProjects;
    private ProgressIndicator pi;

    public ProjectListFragment() {}

    public ProjectListFragment(ProgressIndicator pi) {
        this.pi = pi;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pla = new ProjectListAdapter(getActivity(), pi);
        pla.setTextKey("name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate
            (R.layout.fragment_list_projects, container, false);

        lvProjects = (ListView) v.findViewById(R.id.lvProjects);
        Log.d("DEBUG", "Connecting adapter to " + lvProjects);
        lvProjects.setAdapter(pla);
        lvProjects.setOnItemClickListener(this);
        Log.d("DEBUG", "set on item click listener");
        return v;
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position,
                            long id)
    {
        Log.d("DEBUG", "clicked on item " + position);
        Intent i = new Intent(getActivity(), ProjectDetailsActivity.class);
        Project p = pla.getItem(position);
        Log.d("DEBUG", "putting project = " + p.getObjectId());
        i.putExtra("projectId", p.getObjectId());
        i.putExtra("projectName", p.getName());
        startActivity(i);
    }
}
