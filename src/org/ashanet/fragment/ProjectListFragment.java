package org.ashanet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import org.ashanet.R;
import org.ashanet.fragment.ProjectDetailsFragment;
import org.ashanet.adapter.ProjectListAdapter;
import org.ashanet.interfaces.FragmentNavigation;
import org.ashanet.interfaces.ProgressIndicator;
import org.ashanet.typedef.Project;
import org.ashanet.util.TypeMaps;

public class ProjectListFragment
    extends Fragment
    implements AdapterView.OnItemClickListener
{
    public ProjectListAdapter pla;
    private ListView lvProjects;
    private ProgressIndicator pi;
    private FragmentNavigation fn;
    private TypeMaps tm;
    
    public ProjectListFragment() {}

    public ProjectListFragment(ProgressIndicator pi, FragmentNavigation fn,
                               TypeMaps tm)
    {
        this.pi = pi;
        this.fn = fn;
        this.tm = tm;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pla = new ProjectListAdapter(getActivity(), pi, tm);
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
        Project p = pla.getItem(position);
        fn.pushFragment
            (new ProjectDetailsFragment(pi, p, tm), -1, p.getName());
    }
}
