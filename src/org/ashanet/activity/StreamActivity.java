
package org.ashanet.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.parse.ParseQueryAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.ashanet.AshaNetApp;
import org.ashanet.R;
import org.ashanet.adapter.StreamAdapter;
import org.ashanet.fragment.RespondFragment;
import org.ashanet.typedef.Stream;
import org.ashanet.util.Palette;
import org.ashanet.util.TypeMaps;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class StreamActivity
    extends FragmentActivity
    implements AbsListView.OnScrollListener,
               ParseQueryAdapter.OnQueryLoadListener<Stream>,
               View.OnClickListener
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
    ImageView ivRespondSlotBg;
    TextView tvRespondIcon;
    ImageView ivRespondSlotFg;
    GradientDrawable dgStream;
    GradientDrawable dgRespondSlotFg;
    GradientDrawable dgRespondSlotBg;
    GradientDrawable dgRespondSlot;
    RespondFragment respondFragment;

    private long currentEntry = -1;
    private Palette.COLOR currentColor = Palette.COLOR.GREY;
    private Stream.Type currentType = Stream.Type.PROJECT;
    private long flingToEntry = -1;
    private float position = 0;
    private float velocity = 0;
    private float acceleration = 0;
    private float fallingFrom = 0;
    private float fallingSpeed = 0;
    private float dp = 1;
    private long fallingStarted = 0;
    private int titleWidth = 0;
    private boolean moving = false;
    private boolean flung = false;
    private boolean loaded = false;

    private class Sample {
        public float position;
        public long timeMs;
        Sample(float position, long timeMs) {
            this.position = position;
            this.timeMs = timeMs;
        }
    }
    private ArrayList<Sample> positions;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);
        Log.d("DEBUG", "StreamActivity.onCreate()");
        tm = ((AshaNetApp)getApplication()).typeMaps; 

        Resources rsrc = getResources();
        dp = TypedValue.applyDimension
            (TypedValue.COMPLEX_UNIT_DIP, 100, rsrc.getDisplayMetrics())
            / 100;

        connectWidgets();
        setFadeIn();
        
        positions = new ArrayList<Sample>();
        Log.d("DEBUG", "Creating StreamAdapter(" + this + ", " + tm + ")");
        streamAdapter = new StreamAdapter(this, tm);
        streamAdapter.addOnQueryLoadListener(this);
        lvStream.setAdapter(streamAdapter);
        lvStream.setOnScrollListener(this);
    }
    private final int MOMENT_MS = 100;

    void readPosition() {
         long time = (new Date()).getTime();
         int index = lvStream.getFirstVisiblePosition();
         int height = lvStream.getHeight();
         if (height == 0)
             return;
         else
             streamAdapter.setHeight(height);

         View c = lvStream.getChildAt(0);
         int top = (c == null) ? 0 : c.getTop();
         float fraction = (float) 1.0 - (((float)top + height) / height);
         float newPosition = (fraction + index);
         Sample lastSample;

         // keep a rolling average of velocity and acceleration
         if (positions.size() > 0) {
             lastSample = positions.get(positions.size() - 1);
             if ((time - lastSample.timeMs) > MOMENT_MS)
                 positions.clear();
             else
                 while ((positions.size() > 3) ||
                        ((positions.get(0).timeMs + MOMENT_MS) < time))
                     positions.remove(0);
         }

         if (positions.size() == 0)
             positions.add(new Sample(position, time - MOMENT_MS/4));

         lastSample = positions.get(positions.size() - 1);
         long dTms = time - lastSample.timeMs;
         //Log.d("DEBUG", String.format
               //("dT = %dms or %.3gs, P = %.2g, dP = %.2g",
                //dTms, (float)dTms/1000,
                //newPosition, newPosition - position));

         position = newPosition;
         positions.add(new Sample(position, time));

         float totaldPdT = 0;
         int numdPdT = 0;

         float totaldVdT = 0;
         int numdVdT = 0;
         float lastV = 0;
         float lastdT = 0;

         for (int i = 1; i < positions.size(); i++) {
             // calculate velocity, or dx/dt
             Sample p1 = positions.get(i - 1);
             Sample p2 = positions.get(i);
             float dT = ((float)p2.timeMs - p1.timeMs) / 1000;
             if (dT <= 0)
                 dT = 0.01f;
             float dP = p2.position - p1.position;
             float dPdT = dP/dT;
             totaldPdT = totaldPdT + dPdT;
             numdPdT++;

             // with 3 samples, we can measure acceleration, too.
             if (numdPdT > 1) {
                 float dV = dPdT - lastV;
                 float dVdT = 2.0f * dV/( dT + lastdT );
                 totaldVdT = totaldVdT + dVdT;
                 numdVdT++;
             }
             lastV = dPdT;
             lastdT = dT;
         }
         velocity = (numdPdT == 0) ? 0 : totaldPdT / numdPdT;
         acceleration = (numdVdT == 0) ? 0 : totaldVdT / numdVdT;

         if (flung && !moving && (acceleration < 15)) {
             long direction = (velocity > 0f) ? 1 : -1;
             // this was empirically assessed...
             float idleGuess = velocity * velocity / 32;
             long guessedEntry = 
                 Math.round (position + direction * idleGuess);
             if (guessedEntry > (streamAdapter.getCount() - 1))
                 guessedEntry = streamAdapter.getCount() - 1;
             if (guessedEntry < 0)
                 guessedEntry = 0;
             float distance = Math.abs(guessedEntry - position);
             boolean effective = guessedEntry != currentEntry;
             
             Log.d("DEBUG", String.format
                   ("Fling to %d happening!  Distance = %.2f, P = %.2f, " +
                    "V = %.3g, a = %.3g, Idle guess = %.2f (%s)",
                    guessedEntry, distance,
                    position, velocity, acceleration,
                    idleGuess, (effective ? "it'll make it" : "not enough")
                    ));
             if (effective) {
                 float toGo = Math.abs(guessedEntry - position);
                 final int scrollTimeMs = 300 + (int)Math.round(toGo * 100);
                 final int goToEntry = (int)guessedEntry;
                 moving = true;
                 Log.d("DEBUG", String.format
                       ("SmoothScroll: needs %.2f vs given %.2f: " +
                        "adjust to %d with scroll time = %d",
                        toGo, idleGuess, guessedEntry, scrollTimeMs));
                 lvStream.post
                     (new Runnable() {
                      @Override public void run() {
                          lvStream.smoothScrollToPositionFromTop
                              (goToEntry, 0, scrollTimeMs);
                      }});
                 flingToEntry = guessedEntry;
                 moving = true;
             }
         }
         //Log.d("DEBUG", String.format
               //("Current position is %.1f, vel: %.2g, acc: %.2g (%d samples)",
                //position, velocity, acceleration, numdPdT+1));
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount)
    {
        // Callback method to be invoked when the list or grid has
        // been scrolled.
        //Log.d("DEBUG", "Now visible: " + firstVisibleItem + " (#visible: " + visibleItemCount
              //+ "), total items = " + totalItemCount);
        readPosition();
        if ((moving ? flingToEntry : Math.round(position)) != currentEntry)
            changeLabels();
        setFadeIn();
    }
    
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        readPosition();
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING)
        {
            flung = true;
        }
        else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
        {
           final float distance = Math.abs(currentEntry - position);
           if (distance > 0.001) {
               float fellDistance = Math.abs(position - fallingFrom);
               long now = (new Date()).getTime();
               float duration = ((float)(now - fallingStarted)) / 1000;
               Log.d("DEBUG", String.format
                     ("Idle at %.2f!  V = %.2f, distance = %.2f, t = %.3fs - smooth scrollto %d",
                      position, fallingSpeed, fellDistance, duration,
                      currentEntry));
               lvStream.post
                   (new Runnable() {
                    @Override public void run() {
                        lvStream.smoothScrollToPositionFromTop
                            ((int)currentEntry, 0, (int)Math.round(distance * 1500));
                        flingToEntry = currentEntry;
                        moving = true;
                        flung = false;
                    }});
           }
           else {
               moving = false;
               flung = false;
           }
        }
    }

    void connectWidgets() {
        lvStream = (ListView) findViewById(R.id.lvStream);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvWhen = (TextView) findViewById(R.id.tvWhen);
        ivBottomGradient = (ImageView) findViewById(R.id.ivBottomGradient);
        tvSubtitle = (TextView) findViewById(R.id.tvSubtitle);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        flRespond = (FrameLayout) findViewById(R.id.flRespond);

        ivRespondSlotBg = (ImageView) findViewById(R.id.ivRespondSlotBg);
        ivRespondSlotBg.setOnClickListener(this);
        LayerDrawable ldRespondSlotBg = (LayerDrawable)
            ivRespondSlotBg.getDrawable();
        Log.d("DEBUG", "ldrsb = " + ldRespondSlotBg);
        dgRespondSlotBg = (GradientDrawable)
            ((RotateDrawable)
             ldRespondSlotBg.findDrawableByLayerId(R.id.drRespondSlotBg))
            .getDrawable();
        dgRespondSlot = (GradientDrawable)
            ((RotateDrawable)
             ldRespondSlotBg.findDrawableByLayerId(R.id.drRespondSlot))
            .getDrawable();

        dgStream = (GradientDrawable) ivBottomGradient.getDrawable();

        tvRespondIcon = (TextView) findViewById(R.id.tvRespondIcon);
        tvRespondIcon.setOnClickListener(this);
        ivRespondSlotFg = (ImageView) findViewById(R.id.ivRespondSlotFg);
        dgRespondSlotFg = (GradientDrawable)
            ((RotateDrawable)
             ((LayerDrawable) ivRespondSlotFg.getDrawable())
             .findDrawableByLayerId(R.id.drRespondSlotFg))
            .getDrawable();

        Log.d("DEBUG", "Drawables: " + 
              "dgRespondSlot=" + dgRespondSlot
              + ", dgRespondSlotFg=" + dgRespondSlotFg
              + ", dgRespondSlotBg=" + dgRespondSlotBg
              + ", dgStream=" + dgStream);
    }

    void changeLabels() {
        long oldPosition = currentEntry;
        currentEntry = Math.round(position);
        Stream streamItem = streamAdapter.getItem((int)currentEntry);
        if (streamItem == null) {
            Log.d("DEBUG", "failed to get item at " + currentEntry +
                  ", reverting to " + oldPosition);
            currentEntry = oldPosition;
        }
        else {
            Log.d("DEBUG", "setting up labels with " + streamItem +
                  " (tvTitle = " + tvTitle + ") - we are now currentEntry=" + currentEntry);
            tvTitle.setText(streamItem.getTitle().toUpperCase());
            titleWidth = tvTitle.getWidth();
            tvSubtitle.setText(streamItem.getSubtitle().toUpperCase());
            Log.d("DEBUG", "setting description to " + streamItem.getDescription());
            tvDescription.setText(streamItem.getDescription());
            Log.d("DEBUG", "tvDescription geometry: " +
                  String.format("%dx%d+%.1fx%.1f", tvDescription.getWidth(),
                                tvDescription.getHeight(), tvDescription.getX(),
                                tvDescription.getY()));
            changeColor(Palette.getColor(streamItem.getColorName()));
            if (currentType != streamItem.getType())
                setType(streamItem.getType());
        }
    }

    void changeColor(Palette.COLOR color) {
        Resources rsrc = getResources();
       int lightColor = rsrc.getColor(color.light);
        int mediumColor = rsrc.getColor(color.medium);
        int darkColor = rsrc.getColor(color.dark);
        Log.d("DEBUG", "Change color to " + color +
              String.format(" (light: %-8x)", lightColor));

        tvTitle.setBackgroundColor(lightColor);
        tvSubtitle.setBackgroundColor(lightColor);
        int[] bgGradient = { mediumColor, 0 };
        dgStream.setColors(bgGradient);

        // flRespond;  // will need the message to be passed along

        tvRespondIcon.setTextColor(lightColor);
        dgRespondSlotFg.setColor(lightColor);
        dgRespondSlotBg.setColor(lightColor);
        // doesn't work :-(
        // dgRespondSlot.setStroke(4, darkColor);
    }

    void setType(Stream.Type type) {
        currentType = type;
        Log.d("DEBUG", "Set type to " + type);
        int size = Math.round(28*dp);
        switch (type) {
        case PROJECT:
            tvRespondIcon.setText(R.string.symbol_clam);
            tvRespondIcon.setBackgroundResource(R.drawable.coin_bg);
            tvRespondIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            break;
        case EVENT:
            tvRespondIcon.setText(R.string.icon_rsvp);
            tvRespondIcon.setBackgroundResource(R.drawable.envelope);
            tvRespondIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            size = ViewGroup.LayoutParams.WRAP_CONTENT;
            break;
        }

        RelativeLayout.LayoutParams lpRespondIcon =
            new RelativeLayout.LayoutParams(size, size);
        lpRespondIcon.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpRespondIcon.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int _20dp = Math.round(20 * dp);
        lpRespondIcon.setMargins(0, 0, _20dp, _20dp);
        tvRespondIcon.setLayoutParams(lpRespondIcon);
        //tvRespondIcon.postInvalidate();
    }

    void setFadeIn() {
        float distance = Math.abs
            ((moving ? flingToEntry : currentEntry) - position);

        if (!loaded)
            distance = 1;
        else if ((Math.abs(velocity) > 2) &&
            !( ((velocity < 0) && (position < 0.333)) ||
               (currentEntry == (streamAdapter.getCount() - 1))))
        {
            if (Math.abs(velocity) > 2)
                distance = 1;
            else
                distance = distance * Math.abs(velocity) / 2;
        }
        if ((distance < 1) && (distance > 0.33333)) {
            distance = 1;
        }
        else {
            distance = distance * 3;
        }


        //        Log.d("DEBUG", String.format
        //("Position = %.2f, V = %.2g, a = %.2g, distance = %.2f",
                       //position, velocity, acceleration, distance));
        //int width = tvSubtitle.getWidth();
        tvTitle.setTranslationX
            ( -distance * (tvTitle.getWidth() + 20 * 20) );

        float slotMoves = 30f * dp;
        ivRespondSlotFg.setTranslationX(distance * slotMoves);
        ivRespondSlotFg.setTranslationY(distance * slotMoves);
        ivRespondSlotBg.setTranslationX(distance * slotMoves);
        ivRespondSlotBg.setTranslationY(distance * slotMoves);

        float iconMoves = 50f * dp;
        tvRespondIcon.setTranslationX(distance * iconMoves);
        tvRespondIcon.setTranslationY(-distance * iconMoves);

        float veryClose = distance > 0.5 ? 0 : (1 - distance*2);
        tvDescription.setAlpha(veryClose);
        tvSubtitle.setAlpha(veryClose);
        ivBottomGradient.setAlpha(veryClose);

    }
    public void onLoaded(List<Stream> objects, Exception e) {
        loaded = true;
    }
    public void onLoading() {
        loaded = false;
    }

    private boolean fragmentActive = false;

    @Override
    public void onClick(View v) {
        Log.d("DEBUG", "onClick(" + v + ")");
        if ((v == tvRespondIcon) || (v == ivRespondSlotBg)) {
            animateRespondButton(!fragmentActive);
            if (!fragmentActive)
                activateRespondFragment();
            else
                detachRespondFragment();
        }
        else {
            Log.d("DEBUG", "Not Respond.");
        }
    }

    void animateRespondButton(boolean activating) {
        Log.d("DEBUG", "Respond button to " + activating);
        AnimatorSet set = new AnimatorSet();
        float start = activating ? 0f : 20f*dp;
        float end = activating ? 20f : 0f*dp;
        set.playTogether
            (ObjectAnimator.ofFloat
             (tvRespondIcon, "translationX", start, end)
             .setDuration(250),
             ObjectAnimator.ofFloat
             (tvRespondIcon, "translationY", start, end)
             .setDuration(250)
             );
        set.start();
    }

    void detachRespondFragment() {
        Log.d("DEBUG", "Detaching response fragment.");
        FragmentTransaction ft = getSupportFragmentManager()
            .beginTransaction();
        ft.setCustomAnimations(R.anim.flick_up, R.anim.flick_down);
        ft.remove(respondFragment);
        ft.commit();
        respondFragment = null;
        fragmentActive = false;
    }

    void activateRespondFragment() {
        FragmentTransaction ft = getSupportFragmentManager()
            .beginTransaction();
        ft.setCustomAnimations(R.anim.flick_up, R.anim.flick_down);
        Stream item = streamAdapter.getItem((int)currentEntry);
        Log.d("DEBUG", "Creating RespondFragment");
        respondFragment = new RespondFragment(item);
        Log.d("DEBUG", "Attaching RespondFragment");
        ft.replace(R.id.flRespond, respondFragment);
        ft.commit();
        Log.d("DEBUG", "Committing new RespondFragment");
        fragmentActive = true;
    }
}
