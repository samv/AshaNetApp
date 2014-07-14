
package org.ashanet.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import org.ashanet.R;
import org.ashanet.interfaces.FragmentNavigation;
import org.ashanet.interfaces.ProgressIndicator;

public class AboutFragment extends Fragment {
    private ProgressIndicator pi;
    private FragmentNavigation fn;
    private WebView wvBody;
    boolean loadingFinished = true;
    boolean redirect = false;

    // FIXME - this should go in a base class.
    public AboutFragment(ProgressIndicator pi)
    {
        this.pi = pi;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d("DEBUG", "Inflating AboutFragment");
        View v = inflater.inflate
            (R.layout.fragment_about, container, false);
        wvBody = (WebView) v.findViewById(R.id.wvBody);
        pointPhasers();
        Log.d("DEBUG", "Returning AboutFragment");
        return v;
    }

    public void pointPhasers() {
        wvBody.setWebViewClient
            (new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading
                        (WebView view, String urlNewString)
                    {
                        if (!loadingFinished) {
                            redirect = true;
                        }
                        
                        loadingFinished = false;
                        view.loadUrl(urlNewString);
                        return true;
                    }

                    @Override
                    public void onPageStarted
                        (WebView view, String url, Bitmap facIcon)
                    {
                        loadingFinished = false;
                        pi.setProgressIndicator(-1, true);
                    }
                    
                    @Override
                    public void onPageFinished(WebView view, String url)
                    {
                        if(!redirect){
                            loadingFinished = true;
                        }

                        if(loadingFinished && !redirect){
                            pi.setProgressIndicator(-1, false);
                        } else{
                            redirect = false; 
                        }
                    }
                });
        wvBody.loadUrl("http://www.ashanet.org/index.php?page=about");
    }
}

