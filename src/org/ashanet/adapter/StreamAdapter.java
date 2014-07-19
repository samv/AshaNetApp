
package org.ashanet.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import java.util.HashMap;
import java.util.List;
import org.ashanet.R;
import org.ashanet.interfaces.ProgressIndicator;
import org.ashanet.typedef.Chapter;
import org.ashanet.typedef.Event;
import org.ashanet.typedef.Project;
import org.ashanet.typedef.StockImage;
import org.ashanet.typedef.Stream;
import org.ashanet.util.TypeMaps;

public class StreamAdapter
    extends ParseQueryAdapter<Stream>
{
    TypeMaps tm;
    public StreamAdapter(Context context, TypeMaps tm)
    {
        super(context, Stream.class, R.layout.view_stream_image);
        this.tm = tm;
        Log.d("DEBUG", "new StreamListAdapter");
    }

    /**
    private ArrayList<ImageView> currentViews;

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if (v != null)
            currentViews.set(currentViews.indexOf(v), null);
        while (currentViews.size() <= position) {
            currentViews.add(null);
        }
        v = super.getView(position, v, parent);
        currentViews.set(position, v);
    }
    */

    @Override
    public View getItemView(Stream streamItem, View v, ViewGroup parent) {
        if (v == null)
            v = super.getItemView(streamItem, v, parent);

        ImageView i = (ImageView) v.findViewById(R.id.ivImage);
        Log.d("DEBUG", "Found " + i + " in " + v);

        i.setTag(streamItem.getObjectId());
        String imageId = streamItem.getImageId();
        insertStockImage(imageId, i);
        return v;
    }

    @Override
    public View getNextPageView(View v, ViewGroup parent) {
        if (v == null)
            v = LayoutInflater.from(getContext())
                .inflate(R.layout.view_next_page, parent, false);
        ((TextView)v.findViewById(R.id.text1)).setText
            (R.string.cta_more_projects);
        return v;
    }

    void insertStockImage(String imageId, ImageView ivImage) {
        ivImage.setImageResource(R.drawable.splash);
        ParseQuery<StockImage> query = ParseQuery.getQuery(StockImage.class);
        final ImageView ivImageRef = ivImage;
        query.getInBackground
            (imageId,
             new GetCallback<StockImage>() {
                public void done(StockImage sImage, ParseException e) {
                    if ((e == null) &&
                        (ivImageRef.getTag() == sImage.getObjectId()))
                    {
                        setImageFile(sImage, ivImageRef);
                    } else {
                        Log.d("ERROR", "erp! " + e);
                    }
                }
            });
    }

    void setImageFile(StockImage sImage, ImageView ivImage) {
        final ImageView ivImageRef = ivImage;
        final StockImage sImageRef = sImage;
        sImage.getImageFile().getDataInBackground
            (new GetDataCallback() {
             @Override
             public void done(byte[] data, ParseException e) {
                 if ((e == null) &&
                     (ivImageRef.getTag() == sImageRef.getObjectId()))
                 {
                     Bitmap bmp = BitmapFactory.decodeByteArray
                         (data, 0, data.length);
                     ivImageRef.setImageBitmap(bmp);
                 }
             }});
    }

    @Override
    public void loadNextPage() {
        Log.d("DEBUG", "Loading next page");
        super.loadNextPage();
    }
}
