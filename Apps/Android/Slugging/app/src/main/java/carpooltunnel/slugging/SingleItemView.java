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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SingleItemView extends AppCompatActivity {
    // Declare Variables
    TextView txtDepDay;
    TextView txtDepTime;
    TextView txtFrom;
    TextView txtNumPass;
    TextView txtTo;
    TextView txtDriverUser;
    TextView txtCreatedAt;
    TextView txtUpdatedAt;


    String depDay;
    String depTime;
    String from;
    String numPass;
    String to;
    String driverUser;
    String createdAt;
    String updatedAt;
    ParseObject route;
    public final String TAG = "SIV";
    boolean canBook = true;;
    List<ParseObject> ob;
    final ParseUser me = ParseUser.getCurrentUser();
    final String myUser = me.getUsername().toString();
    Toolbar toolbar;

    public void bookPushToDriver(ParseUser driver, String passenger, String from, String to, String date) {
        ParsePush push = new ParsePush();
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("user", driver);
        push.setQuery(pushQuery);
        push.setMessage(passenger + " has booked your route from "
                + from + " to " + to + " on " + date + ".");
        push.sendInBackground();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item_view);
        // Retrieve data from MainActivity on item click event

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParseRoute");
        query.include("user");
        final List<String> bookers = new ArrayList<String>();
        final Button btn = (Button) findViewById(R.id.book);
        query.getInBackground(i.getStringExtra("routeId"), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your game score
                    route = object;

                    if(route.getList("bookers")!=null){
                        List<String> ary = route.getList("bookers");
                        //Log.e(TAG, "bookers" + ary.toString());
                        if(ary.contains(me.getUsername().toString())){
                            canBook = false;

                        }
                    }
                } else {
                    // something went wrong
                }
            }
        });



        depDay = i.getStringExtra("depDay");
        depTime = i.getStringExtra("depTime");
        from = i.getStringExtra("from");
        numPass = i.getStringExtra("numPass");
        to = i.getStringExtra("to");
        driverUser = i.getStringExtra("driverUser");
        createdAt = i.getStringExtra("createdAt");
        updatedAt = i.getStringExtra("updatedAt");

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(SingleItemView.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Sign up for route?")
                        .setMessage("Do you want to book this route?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (((Integer.parseInt(numPass) - 1) >= 0) && !me.getUsername().equals(driverUser) && canBook) {
                                    Toast.makeText(getApplicationContext(),
                                            "Sent request to route driver!",
                                            Toast.LENGTH_LONG).show();
                                    //int x = Integer.parseInt(route.getString("numPass")) - 1;
                                    route.add("bookers", me.getUsername().toString());
                                    //route.put("numPass", String.valueOf(x));
                                    canBook =false;
                                    ParseUser driver = (ParseUser) route.get("user");
                                    String origin = (String) route.get("from");
                                    String dest = (String) route.get("to");
                                    String date = (String) route.get("depDay");
                                    bookPushToDriver(driver, myUser, origin, dest, date);
                                    try{
                                        route.save();
                                    }catch(ParseException e){ Log.e(TAG, "error saving " + e.toString()); }
                                }
                                else{
                                    Toast.makeText(SingleItemView.this.getApplicationContext(),
                                            "Error, you may already have booked this route or there is no room!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        // Locate the TextViews in singleitemview.xml
        txtDepDay = (TextView) findViewById(R.id.depDay);
        txtDepTime = (TextView) findViewById(R.id.depTime);
        txtFrom = (TextView) findViewById(R.id.from);
        txtNumPass = (TextView) findViewById(R.id.numPass);
        txtTo = (TextView) findViewById(R.id.to);
        txtDriverUser = (TextView) findViewById(R.id.driverUser);
        txtCreatedAt = (TextView) findViewById(R.id.createdAt);
        txtUpdatedAt = (TextView) findViewById(R.id.updatedAt);

        // Load the results into the TextViews
        txtDepDay.setText(depDay);
        txtDepTime.setText(depTime);
        txtFrom.setText(from);
        txtNumPass.setText(numPass);
        txtTo.setText(to);
        txtDriverUser.setText(driverUser);
        //Log.e(TAG, "CA:" + createdAt);
        //Log.e(TAG,"UA:"+updatedAt);
        txtCreatedAt.setText(createdAt);
        txtUpdatedAt.setText(updatedAt);

        //Log.e(TAG, "user:" + name);
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