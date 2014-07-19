
package org.ashanet.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;
import org.ashanet.AshaNetApp;
import org.ashanet.R;
import org.ashanet.adapter.StreamAdapter;
import org.ashanet.util.TypeMaps;

public class StreamActivity
    extends FragmentActivity
    implements AbsListView.OnScrollListener
{
    private StreamAdapter streamAdapter;
    private TypeMaps tm;
    private ListView lvStream;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);
        Log.d("DEBUG", "StreamActivity.onCreate()");
        tm = ((AshaNetApp)getApplication()).typeMaps;
        Log.d("DEBUG", "Creating StreamAdapter(" + this + ", " + tm + ")");
        streamAdapter = new StreamAdapter(this, tm);
        lvStream = (ListView) findViewById(R.id.lvStream);
        lvStream.setAdapter(streamAdapter);
        lvStream.setOnScrollListener(this);
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount)
    {
        // Callback method to be invoked when the list or grid has
        // been scrolled.
        //Log.d("DEBUG", "Now visible: " + firstVisibleItem + " (#visible: " + visibleItemCount
              //+ "), total items = " + totalItemCount);
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.d("DEBUG", "Scroll state changed to " + scrollState);
    }
}
