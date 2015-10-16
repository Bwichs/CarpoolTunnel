package carpooltunnel.slugging;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;
import android.util.Log;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import java.security.MessageDigest;
import android.util.Base64;
import android.widget.Toast;


public class AccountActivity extends AppCompatActivity {
    public static final String TAG = "map";

    final ParseUser user = ParseUser.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        final Button facebookButton = (Button) findViewById(R.id.fbButton);
        if (ParseFacebookUtils.isLinked(user)) {
            Log.d(TAG, "Logged in");
            facebookButton.setText("Go to Account");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });



        TextView un = (TextView) findViewById(R.id.anContent);
        un.setText(user.getUsername());
        final EditText pn = (EditText) findViewById(R.id.pnContent);
        pn.setText(user.getString("phoneNumber"));
        final EditText ct = (EditText) findViewById(R.id.ctContent);
        ct.setText(user.getString("carType"));

        final Button submitButton = (Button) findViewById(R.id.sbutton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String phoneNumber = pn.getText().toString();
                user.put("phoneNumber", phoneNumber);
                pn.setText(phoneNumber);
                String carType = ct.getText().toString();
                user.put("carType", carType);
                ct.setText(carType);
                user.saveInBackground();
            }
        });

        final Button passwordButton = (Button) findViewById(R.id.pwbutton);
        passwordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ParseUser.requestPasswordResetInBackground(user.getEmail(), new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.e(TAG,"Sent email reset");
                            Toast.makeText(getApplicationContext(),
                                    "Successfully sent password reset email!",
                                    Toast.LENGTH_LONG).show();
                            // An email was successfully sent with reset instructions.
                        } else {
                            // Something went wrong. Look at the ParseException to see what's up.
                        }
                    }
                });
            }
        });


        facebookButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                    final List<String> permissions = Arrays.asList("public_profile");
                    ParseFacebookUtils.linkWithReadPermissionsInBackground(ParseUser.getCurrentUser(), AccountActivity.this, permissions, new SaveCallback() {
                        @Override
                        public void done(ParseException ex) {
                            if (ParseFacebookUtils.isLinked(user)) {
                                Log.e(TAG, "Linked");
                            }
                        }
                    });
                }
                else
                {
                    Log.e(TAG,"Pressed");
                    if (ParseFacebookUtils.isLinked(user)) {
                        Log.e(TAG,"Logged in");
                        facebookButton.setText("Go to Account");
                    }
                }
            }
        });
    }

}
