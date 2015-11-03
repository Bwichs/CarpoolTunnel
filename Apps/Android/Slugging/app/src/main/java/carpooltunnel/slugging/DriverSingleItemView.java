package carpooltunnel.slugging;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
    String name;
    String object;
    ParseObject route;
    Intent i;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_single_item_view);
        // Retrieve data from MainActivity on item click event
        i = getIntent();

        //try {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                    "ParseRoute");
            query.include("user");
            // by ascending
            query.orderByAscending("createdAt");
//            ob = query.find();
//            for (ParseObject route : ob) {
//                ParseObject n = new ParseObject("User");
//                n = route.getParseObject("user");
//                object = route.getObjectId();
//                Log.e(TAG, "ObjectID create:"+object);
//                name = n.getString("username");
//            }
            query.getInBackground(i.getStringExtra("routeId"), new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        // object will be your game score
                        route = object;
                        //ParseObject n = new ParseObject("User");
                        ParseObject n = route.getParseObject("user");
                        name = n.getString("username");
                        Log.e(TAG,"create error"+route);

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
        Log.e(TAG, "UA:" + updatedAt);
        txtCreatedAt.setText(createdAt);
        txtUpdatedAt.setText(updatedAt);

        Log.e(TAG, "user:" + name);

        Button mEditButton = (Button) findViewById(R.id.editbutton);
        mEditButton.setOnClickListener(new OnClickListener() {
            public void onClick (View view){

            }
        });

        Button mDeleteButton = (Button) findViewById(R.id.deletebutton);
        mDeleteButton.setOnClickListener(new OnClickListener() {

            public void onClick (View view){
                //delete();
                if(route != null) route.deleteInBackground();

                Intent intent = new Intent(DriverSingleItemView.this, DriverActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    void delete(){

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                "ParseRoute");
        query.getInBackground(i.getStringExtra("routeId"), new GetCallback<ParseObject>() {
            ParseObject delobj;
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your game score
                    delobj = object;
                    if(delobj.equals(route))Log.e(TAG,"delete"+route+" "+delobj);

                } else {
                    Log.e(TAG,"null delete");
                    // something went wrong
                }
            }
        });
    }
}