
package org.ashanet.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.ashanet.R;

public class NavDrawerAdapter
    extends ArrayAdapter<String>
    implements AdapterView.OnItemClickListener
{
    public enum Noun {PROJECTS, EVENTS, CHAPTERS};
    public enum GlobalAction {ABOUT, FEEDBACK, LOGINOUT};
    
    public interface Callbacks {
        abstract void onNounSelected(Noun noun);
        abstract void onGlobalAction(GlobalAction action);
    }

    private int minNoun;
    private int maxNoun;
    private int minAction;
    private int maxAction;
    private Callbacks cb;

    public int getItemId(Noun noun) {
        return noun.ordinal() + minNoun;
    }
    public int getItemId(GlobalAction action) {
        return action.ordinal() + minAction;
    }

    public NavDrawerAdapter(Context context, Callbacks cb) {
        super(context, R.layout.drawer_nav_item);
        this.cb = cb;

        Resources rsrc = context.getResources();

        add(rsrc.getString(R.string.nav_title));

        add(rsrc.getString(R.string.nav_title_find));
        String[] nouns = rsrc.getStringArray(R.array.noun_list);
        minNoun = getCount();
        maxNoun = minNoun + nouns.length - 1;
        addAll(nouns);

        String[] actions = rsrc.getStringArray(R.array.global_actions);
        minAction = getCount();
        maxAction = getCount() + actions.length - 1;
        addAll(actions);
    }

    public int getViewTypeCount() {
        return 4;
    }

    public int getItemViewType(int position) {
        return
            ((position == 0) ? 0 :
             (position == (minNoun - 1)) ? 1 :
             (position >= minNoun) && (position <= maxNoun) ? 2 :
             3);
    }
    private int getViewResourceId(int itemType) {
        switch (itemType) {
        case 0:
            return R.layout.drawer_title;
        case 1:
            return R.layout.drawer_heading;
        case 2:
            return R.layout.drawer_nav_item;
        case 3:
            return R.layout.drawer_global_action;
        default:
            return -1;
        }
    }

    void setupGlobalAction(View view, int actionId) {
        ImageView ivIcon = (ImageView)view.findViewById(R.id.ivIcon);
        ivIcon.setImageResource
            ((actionId == 0) ? R.drawable.question :
             (actionId == 1) ? R.drawable.feedback :
             R.drawable.logged_out);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        int ivt = getItemViewType(position);
        if (convertView == null) {
            int resourceId = getViewResourceId(ivt);
            Log.d("DEBUG", "Inflating " + position + " (type " + ivt + ") with view ID = " + resourceId + "!");
            view = LayoutInflater.from(getContext())
                .inflate(resourceId, parent, false);
            Log.d("DEBUG", "Inflated!");
        }
        // set the text
        String itemText = getItem(position);
        Log.d("DEBUG", "Setting position = " + position + " to '" + itemText + "'");
        TextView tvText1 = (TextView)view.findViewById(android.R.id.text1);
        if (ivt == 1)
            tvText1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        if (ivt == 3)
            setupGlobalAction(view, position - minAction);
        tvText1.setText(itemText);
        Log.d("DEBUG", "done");
        return view;
    }

    public boolean onClick(View v) {
        // TODO
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if ((position >= minNoun) && (position <= maxNoun)) {
            cb.onNounSelected(Noun.values()[position - minNoun]);
        }
        else if ((position >= minAction) && (position <= maxAction)) {
            cb.onGlobalAction(GlobalAction.values()[position - minAction]);
        }
        else {
            Log.d("DEBUG", "Ignoring click to Item " + position + " (" +
                  view + ")");
        }
        // TODO
    }
}
