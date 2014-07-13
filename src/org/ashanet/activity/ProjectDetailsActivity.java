package org.ashanet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import org.ashanet.R;
import org.ashanet.typedef.Project;

public class ProjectDetailsActivity
    extends Activity
{
    Intent intent;
    Project project;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_project_details);
        setProgressBarIndeterminateVisibility(true);

        intent = getIntent();
        String projectId = intent.getStringExtra("projectId");
        String projectName = intent.getStringExtra("projectName");
        if (projectName != null)
            getActionBar().setTitle(projectName);

        Log.d("DEBUG", "got project = " + projectId);

        ParseQuery<Project> q = ParseQuery.getQuery(Project.class);
        q.getInBackground
            (projectId, new GetCallback<Project>() {
                @Override public void done(Project project, ParseException e) {
                    setProgressBarIndeterminateVisibility(false);
                    if (e != null)
                        showParseError(e);
                    else
                        setProject(project);
                }
            });
    }

    public void showParseError(ParseException e) {
        Toast.makeText
            (getApplicationContext(), "Error from Parse.com: " + e,
             Toast.LENGTH_LONG).show();
    }

    public void setProject(Project project) {
        this.project = project;
        displayData(project);
    }

    public void displayData(Project project) {
        Log.d("DEBUG", "displaying project = " + project);
        getActionBar().setTitle(project.getName());
        // TODO - set the project name in the action bar
        //((TextView)findViewById(R.id.tvName)).setText(project.getName());
        ((TextView)findViewById(R.id.tvTypeName)).setText
            (String.format("%d", project.getProjectTypeId()));
        ((TextView)findViewById(R.id.tvFocus)).setText
            (String.format("%d", project.getFocusId()));
        ((TextView)findViewById(R.id.tvChapter)).setText
            (project.getChapterOid());
        ((TextView)findViewById(R.id.tvFunds)).setText
            (String.format("$%.0f", project.getTotalFunds()));
        ((TextView)findViewById(R.id.tvArea)).setText(project.getState());
        WebView wvDescription = ((WebView)findViewById(R.id.wvDescription));
        wvDescription.loadData(project.getDescription(), "text/html", null);
        wvDescription.setBackgroundColor(0x00000000);
        wvDescription.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        ((TextView)findViewById(R.id.tvSecondFocus)).setText
            (project.getSecondaryFocus());
    }
}
