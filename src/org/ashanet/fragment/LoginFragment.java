
package org.ashanet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class LoginFragment
    extends Fragment
    implements AdapterView.OnClickListener
{
    private ProgressIndicator pi;
    private FragmentNavigation fn;

    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    TextView tvLoginError;

    // FIXME - this should go in a base class.
    public LoginFragment(ProgressIndicator pi, FragmentNavigation fn)
    {
        this.pi = pi;
        this.fn = fn;
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
        View v = inflater.inflate
            (R.layout.fragment_login, container, false);
        etUsername = (EditText) v.findViewById(R.id.etUserName);
        etPassword = (EditText) v.findViewById(R.id.etPassword);
        btnLogin = (Button) v.findViewById(R.id.btnLogin);
        tvLoginError = (TextView) v.findViewById(R.id.tvLoginError);
        btnLogin.setOnClickListener(this);
        return v;
    }
    
    @Override
    public void onClick(View v)
    {
        pi.setProgressIndicator(-1, true);
        ParseUser.logInInBackground
            (etUsername.getText().toString(),
             etPassword.getText().toString(),
             new LogInCallback() {
                 public void done(ParseUser user, ParseException e) {
                     if (user != null) {
                         tvLoginError.setText("");
                         Toast.makeText
                             (getActivity(),
                              String.format(getActivity().getResources()
                                            .getString(R.string.msg_logged_in),
                                            user.getUsername()),
                              Toast.LENGTH_LONG).show();
                         fn.popFragment(R.string.title_login);
                     } else {
                         tvLoginError.setText("Error logging in: " + e);
                     }
                     pi.setProgressIndicator(-1, false);
                 }
             });
    }

}

