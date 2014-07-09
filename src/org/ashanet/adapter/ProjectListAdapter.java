
package org.ashanet.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import org.ashanet.R;
import org.ashanet.typedef.Project;

public class ProjectListAdapter extends ParseQueryAdapter<Project> {
    public ProjectListAdapter(Context context) {
        super(context, Project.class, R.layout.view_project);
        Log.d("DEBUG", "new ProjectListAdapter");
    }

    @Override
    public View getItemView(Project X, View v, ViewGroup parent) {
        if (v == null)
            v = super.getItemView(X, v, parent);

        ((TextView)v.findViewById(R.id.tvName)).setText(X.getName());
        ((TextView)v.findViewById(R.id.tvState)).setText(X.getState());
        ((TextView)v.findViewById(R.id.tvTypeName)).setText("Social Good");
        ((TextView)v.findViewById(R.id.tvFundedBy)).setText("San Francisco");
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

}
