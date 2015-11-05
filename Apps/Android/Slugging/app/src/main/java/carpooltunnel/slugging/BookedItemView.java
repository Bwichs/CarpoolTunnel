package carpooltunnel.slugging;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class BookedItemView extends AppCompatActivity {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_item_view);
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
                new AlertDialog.Builder(BookedItemView.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Unbook route?")
                        .setMessage("Do you want to unbook this route?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),
                                        "Successfully unbooked route!",
                                        Toast.LENGTH_LONG).show();
                                int x = Integer.parseInt(route.getString("numPass")) + 1;
                                List<String> temp = new ArrayList<String>();
                                temp.add(me.getUsername().toString());
                                route.removeAll("bookers", temp);
                                route.put("numPass", String.valueOf(x));
                                ParseUser driver = (ParseUser) route.get("user");
                                String origin = (String) route.get("from");
                                String dest = (String) route.get("to");
                                String date = (String) route.get("depDay");
                                unBookPushToDriver(driver, myUser, origin, dest, date);
                                try{
                                    route.save();
                                }catch(ParseException e){ Log.e(TAG, "error saving " + e.toString()); }
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
    }

    public void unBookPushToDriver(ParseUser driver, String passenger, String from, String to, String date) {
        ParsePush push = new ParsePush();
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("user", driver);
        push.setQuery(pushQuery);
        push.setMessage(passenger + " has unbooked your route from "
                + from + " to " + to + " on " + date + ".");
        push.sendInBackground();
    }
}
