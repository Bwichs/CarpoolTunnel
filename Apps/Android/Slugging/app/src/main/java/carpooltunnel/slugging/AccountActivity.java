package carpooltunnel.slugging;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;




public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


        final ParseUser user = ParseUser.getCurrentUser();
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
                ParseUser.requestPasswordResetInBackground("myemail@example.com", new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // An email was successfully sent with reset instructions.
                        } else {
                            // Something went wrong. Look at the ParseException to see what's up.
                        }
                    }
                });
            }
        });

        final Button facebookButton = (Button) findViewById(R.id.fbButton);
        facebookButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!ParseFacebookUtils.isLinked(user)) {
                    List<String> permissions = Arrays.asList("public_profile");
                    ParseFacebookUtils.linkWithReadPermissionsInBackground(user, AccountActivity.this, permissions, new SaveCallback() {
                        @Override
                        public void done(ParseException ex) {
                            if (ParseFacebookUtils.isLinked(user)) {
                                facebookButton.setText("Go to Account");
                            }
                        }
                    });
                }
                else
                {
                    /*
                    GraphRequest request = GraphRequest.newMeRequest(
                            accessToken,
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    // Application code
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,link");
                    request.setParameters(parameters);
                    request.executeAsync();
                    */
                }
            }
        });
    }

}
