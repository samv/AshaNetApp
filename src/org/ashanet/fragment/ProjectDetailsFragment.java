package org.ashanet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import org.ashanet.R;
import org.ashanet.interfaces.ProgressIndicator;
import org.ashanet.typedef.Chapter;
import org.ashanet.typedef.Project;
import org.ashanet.typedef.FocusType;
import org.ashanet.util.TypeMaps;

public class ProjectDetailsFragment
    extends Fragment
{
    Intent intent;
    Project project;
    ProgressIndicator pi;
    TypeMaps tm;
    View frag;

    public ProjectDetailsFragment(ProgressIndicator pi, Project p,
                                  TypeMaps tm)
    {
        this.pi = pi;
        project = p;
        this.tm = tm;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        frag = inflater.inflate
            (R.layout.fragment_project_details, container, false);
        setProject(project);
        return frag;
    }

    public void setProject(Project project) {
        this.project = project;
        displayData(project);
    }

    public void displayData(Project project) {
        Log.d("DEBUG", "displaying project = " + project);
        // getActionBar().setTitle(project.getName());
        // TODO - set the project name in the action bar
        //((TextView)findViewById(R.id.tvName)).setText(project.getName());
        ((TextView)frag.findViewById(R.id.tvTypeName)).setText
            (tm.getProjectType(project.getProjectTypeId()).getTitle());
        FocusType ft = tm.getFocusType(project.getFocusId());
        TextView tvFocus = (TextView) frag.findViewById(R.id.tvFocus);
        tvFocus.setText((ft != null) ? ft.getTitle() : "(unknown)");
        Chapter chapter = tm.getChapter(project.getChapterOid());
        ((TextView)frag.findViewById(R.id.tvChapter)).setText
            ((chapter != null) ? chapter.getName() : "(not funded yet)");
        ((TextView)frag.findViewById(R.id.tvFunds)).setText
            (String.format("$%.0f", project.getTotalFunds()));
        ((TextView)frag.findViewById(R.id.tvArea)).setText(project.getState());
        WebView wvDescription =
            ((WebView)frag.findViewById(R.id.wvDescription));
        wvDescription.loadData(project.getDescription(), "text/html", null);
        wvDescription.setBackgroundColor(0x00000000);
        wvDescription.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        ((TextView)frag.findViewById(R.id.tvSecondFocus)).setText
            (project.getSecondaryFocus());
    }
}
