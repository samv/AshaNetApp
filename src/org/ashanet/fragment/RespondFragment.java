
package org.ashanet.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import org.ashanet.R;
import org.ashanet.typedef.Stream;
import org.ashanet.util.Palette;

public class RespondFragment extends Fragment {
    Stream streamItem;
    boolean isRsvp;
    boolean isDonate;

    GradientDrawable dgRespondBg;  // R.drawable.respond_bg/rotate/gradient
    EditText etAmount;
    EditText etEmail;
    ImageButton ibPayByCCRadio;
    ImageButton ibPayByPPRadio;
    Button btnContinue;
    TextView tvPayWith;
    TextView tvQuestion;
    TextView tvPromise;

    public RespondFragment(Stream streamItem) {
        this.streamItem = streamItem;
        isRsvp = (streamItem.getEventId() != null);
        isDonate = (streamItem.getProjectId() != null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate
            (isDonate ? R.layout.fragment_donate : R.layout.fragment_rsvp,
             container, false);
        Log.d("DEBUG", "inflated a " + (isDonate ? "donate" : "RSVP") +
              " view");
        connectWidgets(v);
        setColor(container.getContext(),
                 Palette.getColor(streamItem.getColorName()));
        return v;
    }

    void setColor(Context c, Palette.COLOR color) {
        Resources rsrc = c.getResources();
        int lightColor = rsrc.getColor(color.light);
        int mediumColor = rsrc.getColor(color.medium);
        int darkColor = rsrc.getColor(color.dark);
        int chalk = rsrc.getColor(R.color.chalk);
        dgRespondBg.setColor(mediumColor);
        btnContinue.setBackgroundColor(lightColor);
        EditText et;
        if (isDonate) {
            et = etAmount;
            tvPayWith.setTextColor(lightColor);
        }
        else {
            et = etEmail;
            tvQuestion.setTextColor(lightColor);
            tvPromise.setTextColor(lightColor);
        }
        et.setBackgroundColor(darkColor);
        et.setTextColor(chalk);
        et.setHintTextColor(lightColor);
    }

    void connectWidgets(View v) {
        dgRespondBg = (GradientDrawable)
            ((RotateDrawable)
             ((LayerDrawable) v.getBackground())
             .findDrawableByLayerId(R.id.drRespondBg))
            .getDrawable();
        btnContinue = (Button) v.findViewById(R.id.btnContinue);
        if (isDonate)
            connectDonateWidgets(v);
        else
            connectRsvpWidgets(v);
    }

    void connectDonateWidgets(View v) {
        etAmount = (EditText) v.findViewById(R.id.etAmount);
        tvPayWith = (TextView) v.findViewById(R.id.tvPayWith);
        ibPayByCCRadio = (ImageButton) v.findViewById(R.id.ibPayByCCRadio);
        ibPayByPPRadio = (ImageButton) v.findViewById(R.id.ibPayByPPRadio);
    }

    void connectRsvpWidgets(View v) {
        etEmail = (EditText) v.findViewById(R.id.etEmail);
        tvQuestion = (TextView) v.findViewById(R.id.tvQuestion);
        tvPromise = (TextView) v.findViewById(R.id.tvPromise);
    }
}
