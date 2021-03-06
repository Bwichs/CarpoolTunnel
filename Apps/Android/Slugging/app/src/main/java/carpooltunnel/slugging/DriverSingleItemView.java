package carpooltunnel.slugging;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class DriverSingleItemView extends AppCompatActivity {
    // Declare Variables
    TextView txtDepDay;
    TextView txtDepTime;
    TextView txtFrom;
    TextView txtNumPass;
    TextView txtTo;
    TextView txtDriverUser;
    TextView txtCreatedAt;
    TextView txtUpdatedAt;
    TextView driverUserLabel;

    String depDay;
    String depTime;
    String from;
    String numPass;
    String to;
    String driverUser;
    String createdAt;
    String updatedAt;

    public final String TAG = "DSIV";
    List<ParseObject> ob;
    String name = "";
    ParseObject route;
    Intent i;
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_single_item_view);
        // Retrieve data from MainActivity on item click event
        i = getIntent();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //try {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                    "ParseRoute");
            query.include("user");
            // by ascending
            query.orderByAscending("createdAt");
            txtDriverUser = (TextView) findViewById(R.id.driverUser);
            query.getInBackground(i.getStringExtra("routeId"), new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        // object will be your game score
                        route = object;
                        //ParseObject n = new ParseObject("User");
                        ParseObject n = route.getParseObject("user");
                        name = n.getString("username");

                        Log.e(TAG,"create error"+route);

                        //ParseObject n = new ParseObject("User");
                        //ParseObject n = route.getParseObject("user");
                    } else {
                        Log.e(TAG,"null get");
                        // something went wrong
                    }
                }
            });

        //} //catch (ParseException e) {

        //}
        depDay = i.getStringExtra("depDay");
        depTime = i.getStringExtra("depTime");
        from = i.getStringExtra("from");
        numPass = i.getStringExtra("numPass");
        to = i.getStringExtra("to");
        driverUser = i.getStringExtra("driverUser");
        createdAt = i.getStringExtra("createdAt");
        updatedAt = i.getStringExtra("updatedAt");

        // Locate the TextViews in singleitemview.xml
        txtDepDay = (TextView) findViewById(R.id.depDay);
        txtDepTime = (TextView) findViewById(R.id.depTime);
        txtFrom = (TextView) findViewById(R.id.from);
        txtNumPass = (TextView) findViewById(R.id.numPass);
        txtTo = (TextView) findViewById(R.id.to);

        txtCreatedAt = (TextView) findViewById(R.id.createdAt);
        txtUpdatedAt = (TextView) findViewById(R.id.updatedAt);
        driverUserLabel = (TextView) findViewById(R.id.driverUserlabel);
        // Load the results into the TextViews
        txtDepDay.setText(depDay);
        txtDepTime.setText(depTime);
        txtFrom.setText(from);
        txtNumPass.setText(numPass);
        txtTo.setText(to);
        driverUserLabel.setText("Driver: " + ParseUser.getCurrentUser().getUsername());

        txtCreatedAt.setText(createdAt);
        txtUpdatedAt.setText(updatedAt);

        //Log.e(TAG, "user:" + name);

//        Button mEditButton = (Button) findViewById(R.id.editbutton);
//        mEditButton.setOnClickListener(new OnClickListener() {
//            public void onClick(View view) {
//
//            }
//        });

        Button mDeleteButton = (Button) findViewById(R.id.deletebutton);
        mDeleteButton.setOnClickListener(new OnClickListener() {

            public void onClick (View view){

                new AlertDialog.Builder(DriverSingleItemView.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete?")
                        .setMessage("Are you sure you wish to delete this route?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (route != null) {
                                    pushToEachPassenger(route);
                                    route.deleteInBackground();
                                    Intent intent = new Intent(DriverSingleItemView.this, DriverActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        Button pendingButton = (Button) findViewById(R.id.pendingbutton);
        pendingButton.setOnClickListener(new OnClickListener() {

            public void onClick (View view){

                Intent intent = new Intent(DriverSingleItemView.this, DriverPending.class);
                // Pass all data rank
                intent.putExtra("routeId",
                        (route.getObjectId()));
                intent.putExtra("verified", "false");
                // Start SingleItemView Class
                startActivity(intent);
                finish();
            }
        });

        Button verifiedButton = (Button) findViewById(R.id.verifiedbutton);
        verifiedButton.setOnClickListener(new OnClickListener() {

            public void onClick (View view){

                Intent intent = new Intent(DriverSingleItemView.this, DriverPending.class);
                // Pass all data rank
                intent.putExtra("routeId",
                        (route.getObjectId()));
                intent.putExtra("verified", "true");
                // Start SingleItemView Class
                startActivity(intent);
                finish();
            }
        });
    }

    // Sends push notification to each Passenger that booked upon Route Delete.
    public void pushToEachPassenger(ParseObject route){
        List<String> bookers = route.getList("bookers");
        List<String> passengers = route.getList("passengers");
        final String driver = ParseUser.getCurrentUser().getUsername();
        final String origin = (String) route.get("from");
        final String dest = (String) route.get("to");
        final String date = (String) route.get("depDay");
        if(bookers != null) {
            for (String booker : bookers) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("email", booker);
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            deletePushToPassenger(objects.get(0), driver, origin, dest, date);
                        } else {
                            Log.e(TAG, "get booker null");
                        }
                    }
                });
            }
        }
        if(passengers != null) {
            for (String passenger : passengers) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("email", passenger);
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            deletePushToPassenger(objects.get(0), driver, origin, dest, date);
                        } else {
                            Log.e(TAG, "get pass null");
                        }
                    }
                });
            }
        }
    }
    public void deletePushToPassenger(ParseUser passenger, String driver, String from, String to, String date) {
        ParsePush push = new ParsePush();
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("user", passenger);
        push.setQuery(pushQuery);
        push.setMessage(driver + " has deleted your booked route from "
                + from + " to " + to + " on " + date + ".");
        push.sendInBackground();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}