package carpooltunnel.slugging;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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

    public final String TAG = "SIV";
    List<ParseObject> ob;
    String name;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item_view);
        // Retrieve data from MainActivity on item click event
        Intent i = getIntent();

        try {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                    "ParseRoute");
            query.include("user");
            // by ascending
            query.orderByAscending("createdAt");
            ob = query.find();
            for (ParseObject route : ob) {
                ParseObject n = new ParseObject("User");
                n = route.getParseObject("user");
                name = n.getString("username");
            }
        }
        catch(ParseException e){

        }
        depDay = i.getStringExtra("depDay");
        depTime = i.getStringExtra("depTime");
        from = i.getStringExtra("from");
        numPass = i.getStringExtra("numPass");
        to = i.getStringExtra("to");
        driverUser = i.getStringExtra(name);
        createdAt = i.getStringExtra("createdAt");
        updatedAt = i.getStringExtra("updatedAt");

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
        txtDriverUser.setText(name);
        Log.e(TAG, "CA:" + createdAt);
        Log.e(TAG,"UA:"+updatedAt);
        txtCreatedAt.setText(createdAt);
        txtUpdatedAt.setText(updatedAt);

        Log.e(TAG, "user:" + name);
    }
}