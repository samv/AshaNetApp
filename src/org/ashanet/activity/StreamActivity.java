
package org.ashanet.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ListView;
import org.ashanet.AshaNetApp;
import org.ashanet.R;
import org.ashanet.adapter.StreamAdapter;
import org.ashanet.util.TypeMaps;

public class StreamActivity extends FragmentActivity {
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
    }
}
