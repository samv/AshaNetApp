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
import org.ashanet.activity.EventDetailsActivity;
import org.ashanet.adapter.EventListAdapter;
import org.ashanet.interfaces.ProgressIndicator;
import org.ashanet.typedef.Event;

public class EventListFragment
    extends Fragment
    implements AdapterView.OnItemClickListener
{
    public EventListAdapter pla;
    private ListView lvEvents;
    private ProgressIndicator pi;

    public EventListFragment() {}

    public EventListFragment(ProgressIndicator pi) {
        this.pi = pi;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pla = new EventListAdapter(getActivity(), pi);
        pla.setTextKey("name");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate
            (R.layout.fragment_list_events, container, false);

        lvEvents = (ListView) v.findViewById(R.id.lvEvents);
        Log.d("DEBUG", "Connecting adapter to " + lvEvents);
        lvEvents.setAdapter(pla);
        lvEvents.setOnItemClickListener(this);
        Log.d("DEBUG", "set on item click listener");
        return v;
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position,
                            long id)
    {
        Log.d("DEBUG", "clicked on item " + position);
        Intent i = new Intent(getActivity(), EventDetailsActivity.class);
        Event p = pla.getItem(position);
        Log.d("DEBUG", "putting event = " + p.getObjectId());
        i.putExtra("eventId", p.getObjectId());
        i.putExtra("eventName", p.getName());
        startActivity(i);
    }
}
