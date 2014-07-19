
package org.ashanet.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.ashanet.AshaNetApp;
import org.ashanet.R;
import org.ashanet.adapter.StreamAdapter;
import org.ashanet.typedef.Stream;
import org.ashanet.util.TypeMaps;

public class StreamActivity
    extends FragmentActivity
    implements AbsListView.OnScrollListener
{
    private StreamAdapter streamAdapter;
    private TypeMaps tm;
    private ListView lvStream;

    TextView tvTitle;
    TextView tvWhen;
    ImageView ivBottomGradient;
    TextView tvSubtitle;
    TextView tvDescription;
    FrameLayout flRespond;
    ImageView ivRespondButton;

    private long currentEntry = -1;
    private double position = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);
        Log.d("DEBUG", "StreamActivity.onCreate()");
        tm = ((AshaNetApp)getApplication()).typeMaps;
        connectWidgets();
        Log.d("DEBUG", "Creating StreamAdapter(" + this + ", " + tm + ")");
        streamAdapter = new StreamAdapter(this, tm);
        lvStream = (ListView) findViewById(R.id.lvStream);
        lvStream.setAdapter(streamAdapter);
        lvStream.setOnScrollListener(this);
    }

    double currentIndex() {
        int index = lvStream.getFirstVisiblePosition();
        int height = lvStream.getHeight();
        Log.d("DEBUG", "index is " + index + ", height is " + height);
        if (height == 0)
            return 0;
        View c = lvStream.getChildAt(0);
        int top = (c == null) ? 0 : c.getTop();
        double fraction = 1.0 - (((double)top + height) / height);
        //Log.d("DEBUG", "top is " + top);
        return fraction + index;
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount)
    {
        // Callback method to be invoked when the list or grid has
        // been scrolled.
        //Log.d("DEBUG", "Now visible: " + firstVisibleItem + " (#visible: " + visibleItemCount
              //+ "), total items = " + totalItemCount);
        position = currentIndex();
        if (Math.round(position) != currentEntry)
            changeLabels();
        setFadeIn();
    }
    
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            Log.d("DEBUG", "Scroll state changed to " + scrollState);   
        }
    }

    void connectWidgets() {
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvWhen = (TextView) findViewById(R.id.tvWhen);
        ivBottomGradient = (ImageView) findViewById(R.id.ivBottomGradient);
        tvSubtitle = (TextView) findViewById(R.id.tvSubtitle);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        flRespond = (FrameLayout) findViewById(R.id.flRespond);
        ivRespondButton = (ImageView) findViewById(R.id.ivRespondButton);
    }

    void changeLabels() {
        long oldPosition = currentEntry;
        currentEntry = Math.round(position);
        Stream streamItem = streamAdapter.getItem((int)currentEntry);
        if (streamItem == null)
            currentEntry = oldPosition;
        else {
            Log.d("DEBUG", "setting up labels with " + streamItem +
                  " (tvTitle = " + tvTitle + ")");
            tvTitle.setText(streamItem.getTitle());
            tvSubtitle.setText(streamItem.getSubtitle());
            tvDescription.setText(streamItem.getDescription());
        }
    }

    void setFadeIn() {
        
    }
}
