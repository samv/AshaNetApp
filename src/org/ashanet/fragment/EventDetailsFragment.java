package org.ashanet.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.ashanet.interfaces.ProgressIndicator;
import org.ashanet.typedef.Event;

public class EventDetailsFragment
    extends Fragment
{
    Intent intent;
    Event event;
    ImageView ivEventImage;
    Button btnGetTickets;
    ProgressIndicator pi;
    View frag;

    public EventDetailsFragment(ProgressIndicator pi, Event e) {
        this.pi = pi;
        event = e;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        frag = inflater.inflate
            (R.layout.fragment_event_details, container, false);

        // TODO Up Navigation

        // if (pi)
        //    pi.setProgressIndicator(-1, true);

        ivEventImage = (ImageView) frag.findViewById(R.id.ivEventImage);
        btnGetTickets = (Button) frag.findViewById(R.id.btnGetTickets);
        setEvent(event);
        return frag;
    }

    public void setEvent(Event event) {
        this.event = event;
        if (event.getImageFile() != null) {
            if (pi != null)
                pi.setProgressIndicator(-1, true);
            event.getImageFile().getDataInBackground
                (new GetDataCallback() {
                 @Override
                 public void done(byte[] data, ParseException e) {
                     Bitmap bmp = BitmapFactory.decodeByteArray
                         (data, 0, data.length);
                     ivEventImage.setImageBitmap(bmp);
                     if (pi != null)
                         pi.setProgressIndicator(-1, false);
                 }});
        }
        displayData(event);
    }

    public void displayData(Event event) {
        Log.d("DEBUG", "displaying event = " + event);
        // TODO - set the event name in the action bar
        //getActionBar().setTitle(event.getName());
        String address = event.getAddress();
        ((TextView)frag.findViewById(R.id.tvAddress)).setText
            (address == null ? "t.b.a." : address);
        WebView wvDescription =
            ((WebView)frag.findViewById(R.id.wvDescription));
        wvDescription.loadData(event.getDescription(), "text/html", null);
        wvDescription.setBackgroundColor(0x00000000);
        wvDescription.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        Date eventStart = event.getEventStart();
        ((TextView)frag.findViewById(R.id.tvEventStart)).setText
            (eventStart == null ? "t.b.a." :
             new SimpleDateFormat
             ("EEE, LLL d MMM, yyyy h:ma",
              Locale.getDefault()).format(eventStart));
    }
}
