package org.ashanet.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
        setContentView(R.layout.activity_project_details);

        intent = getIntent();
        String projectId = intent.getStringExtra("projectId");

        Log.d("DEBUG", "got project = " + projectId);

        ParseQuery<Project> q = ParseQuery.getQuery(Project.class);
        q.getInBackground
            (projectId, new GetCallback<Project>() {
                @Override public void done(Project project, ParseException e) {
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

    public final String HIPSTER_IPSUM = "Food truck 90's Pinterest flexitarian keffiyeh. +1 sartorial art party Tumblr. Cred yr squid food truck, YOLO PBR&B Schlitz Intelligentsia typewriter fixie messenger bag biodiesel Kickstarter. Wayfarers umami readymade, chia Godard Portland fashion axe. Fashion axe YOLO PBR&B, fixie American Apparel shabby chic ethnic paleo. Lomo ethical VHS, Wes Anderson Odd Future crucifix plaid. VHS put a bird on it semiotics church-key, hoodie single-origin coffee yr leggings forage dreamcatcher shabby chic pug chia craft beer Shoreditch.\n\nTypewriter forage biodiesel, asymmetrical seitan paleo selvage lomo quinoa McSweeney's. Yr bitters squid, freegan pickled Wes Anderson organic aesthetic XOXO ugh jean shorts iPhone beard Brooklyn. Carles distillery deep v, single-origin coffee crucifix freegan wayfarers beard disrupt. Hashtag whatever next level kale chips scenester. 90's mustache fanny pack, wayfarers small batch chia sustainable. Roof party raw denim Neutra gastropub synth whatever small batch, authentic iPhone scenester. Tonx viral synth fingerstache, before they sold out bespoke tattooed ennui ugh Tumblr Godard.\n\nCred dreamcatcher gastropub authentic skateboard. Cliche try-hard narwhal retro Wes Anderson deep v. Kickstarter 3 wolf moon yr tofu flexitarian. Gluten-free flexitarian pour-over, slow-carb farm-to-table photo booth fap pork belly before they sold out High Life Banksy. Viral pickled yr flexitarian, before they sold out retro letterpress vinyl pork belly try-hard DIY American Apparel Intelligentsia typewriter. Trust fund Neutra +1, freegan typewriter High Life Portland locavore church-key PBR. Helvetica ethical slow-carb, sriracha whatever Etsy typewriter occupy cliche direct trade.";

    public void displayData(Project project) {
        Log.d("DEBUG", "displaying project = " + project);
        // TODO - set the project name in the action bar
        //((TextView)findViewById(R.id.tvName)).setText(project.getName());
        ((TextView)findViewById(R.id.tvTypeName)).setText("Social Good");
        ((TextView)findViewById(R.id.tvFocus)).setText("Teaching Kids");
        ((TextView)findViewById(R.id.tvChapter)).setText("San Francisco");
        ((TextView)findViewById(R.id.tvPurpose)).setText
            ("To make the world a better place");
        ((TextView)findViewById(R.id.tvYear)).setText("1990");
        ((TextView)findViewById(R.id.tvArea)).setText(project.getState());

        ((TextView)findViewById(R.id.tvDescription)).setText(HIPSTER_IPSUM);
    }
}
