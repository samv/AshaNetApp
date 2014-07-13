package org.ashanet.activity;

import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.ashanet.R;
import org.ashanet.typedef.Event;

public class EventDetailsActivity
    extends FragmentActivity
{
    Intent intent;
    Event event;
    ImageView ivEventImage;
    Button btnGetTickets;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_event_details);
        setProgressBarIndeterminateVisibility(true);

        intent = getIntent();
        String eventId = intent.getStringExtra("eventId");
        String eventName = intent.getStringExtra("eventName");
        if (eventName != null)
            getActionBar().setTitle(eventName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        Log.d("DEBUG", "got event = " + eventId);

        ParseQuery<Event> q = ParseQuery.getQuery(Event.class);
        q.getInBackground
            (eventId, new GetCallback<Event>() {
                @Override public void done(Event event, ParseException e) {
                    setProgressBarIndeterminateVisibility(false);
                    if (e != null)
                        showParseError(e);
                    else
                        setEvent(event);
                }
            });
        ivEventImage = (ImageView) findViewById(R.id.ivEventImage);
        btnGetTickets = (Button) findViewById(R.id.btnGetTickets);
        //btnGetTickets.setOnClickListener(this);
    }

    public void showParseError(ParseException e) {
        Toast.makeText
            (getApplicationContext(), "Error from Parse.com: " + e,
             Toast.LENGTH_LONG).show();
    }

    public void setEvent(Event event) {
        this.event = event;
        if (event.getImageFile() != null) {
            event.getImageFile().getDataInBackground
                (new GetDataCallback() {
                 @Override
                 public void done(byte[] data, ParseException e) {
                     Bitmap bmp = BitmapFactory.decodeByteArray
                         (data, 0, data.length);
                     ivEventImage.setImageBitmap(bmp);
                 }});
        }
        displayData(event);
    }

    public void displayData(Event event) {
        Log.d("DEBUG", "displaying event = " + event);
        getActionBar().setTitle(event.getName());
        // TODO - set the event name in the action bar
        //((TextView)findViewById(R.id.tvName)).setText(event.getName());
        String address = event.getAddress();
        ((TextView)findViewById(R.id.tvAddress)).setText
            (address == null ? "t.b.a." : address);
        WebView wvDescription = ((WebView)findViewById(R.id.wvDescription));
        wvDescription.loadData(event.getDescription(), "text/html", null);
        wvDescription.setBackgroundColor(0x00000000);
        wvDescription.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        Date eventStart = event.getEventStart();
        ((TextView)findViewById(R.id.tvEventStart)).setText
            (eventStart == null ? "t.b.a." :
             new SimpleDateFormat
             ("EEE, LLL d MMM, yyyy h:ma",
              Locale.getDefault()).format(eventStart));
    }
}
