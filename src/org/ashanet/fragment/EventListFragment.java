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
import android.widget.Toast;
import org.ashanet.R;
import org.ashanet.adapter.EventListAdapter;
import org.ashanet.fragment.EventDetailsFragment;
import org.ashanet.interfaces.FragmentNavigation;
import org.ashanet.interfaces.ProgressIndicator;
import org.ashanet.typedef.Event;
import org.ashanet.util.TypeMaps;

public class EventListFragment
    extends Fragment
    implements AdapterView.OnItemClickListener
{
    public EventListAdapter ela;
    private ListView lvEvents;
    private ProgressIndicator pi;
    private FragmentNavigation fn;
    private TypeMaps tm;

    public EventListFragment() {}

    public EventListFragment(ProgressIndicator pi, FragmentNavigation fn,
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
        ela = new EventListAdapter(getActivity(), pi, tm);
        ela.setTextKey("name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate
            (R.layout.fragment_list_events, container, false);

        lvEvents = (ListView) v.findViewById(R.id.lvEvents);
        Log.d("DEBUG", "Connecting adapter to " + lvEvents);
        lvEvents.setAdapter(ela);
        lvEvents.setOnItemClickListener(this);
        Log.d("DEBUG", "set on item click listener");
        return v;
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position,
                            long id)
    {
        Log.d("DEBUG", "clicked on item " + position);
        Event e = ela.getItem(position);
        fn.pushFragment
            (new EventDetailsFragment(pi, e, tm),
             R.string.title_project_details);
    }
}
