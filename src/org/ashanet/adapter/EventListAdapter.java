
package org.ashanet.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import java.util.List;
import org.ashanet.R;
import org.ashanet.interfaces.ProgressIndicator;
import org.ashanet.typedef.Event;

// fixme: parts of this could probably be merged with
//        ProjectListAdapter as a generic, so that just 
//        getItemView need be specified by each fragment.
public class EventListAdapter
    extends ParseQueryAdapter<Event>
    implements ParseQueryAdapter.OnQueryLoadListener<Event>
{
    ProgressIndicator pi;
    public EventListAdapter(Context context, ProgressIndicator pi)
    {
        super(context, Event.class, R.layout.view_event);
        if (pi != null) {
            addOnQueryLoadListener(this);
            this.pi = pi;
        }
        Log.d("DEBUG", "new EventListAdapter");
    }

    @Override
    public View getItemView(Event X, View v, ViewGroup parent) {
        if (v == null)
            v = super.getItemView(X, v, parent);

        ((TextView)v.findViewById(R.id.tvName)).setText(X.getName());
        ((TextView)v.findViewById(R.id.tvAddress)).setText(X.getAddress());
        ((TextView)v.findViewById(R.id.tvChapter)).setText
            ("San Francisco");
        ((TextView)v.findViewById(R.id.tvWhen)).setText
            (X.getEventStart().toString());
        return v;
    }

    @Override
    public View getNextPageView(View v, ViewGroup parent) {
        if (v == null)
            v = LayoutInflater.from(getContext())
                .inflate(R.layout.view_next_page, parent, false);
        ((TextView)v.findViewById(R.id.text1)).setText
            (R.string.cta_more_events);
        return v;
    }

    public void onLoaded(List<Event> objects, Exception e) {
        pi.setProgressIndicator(-1, false);
    }

    public void onLoading() {
        pi.setProgressIndicator(-1, true);
    }

    @Override
    public void loadNextPage() {
        Log.d("DEBUG", "Loading next page");
        super.loadNextPage();
    }
}
