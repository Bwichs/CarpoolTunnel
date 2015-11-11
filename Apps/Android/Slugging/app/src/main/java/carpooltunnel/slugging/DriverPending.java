package carpooltunnel.slugging;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DriverPending extends AppCompatActivity {

    ListView listview;
    List<ParseObject> ob;
    DriverPendingAdapter adapter;
    public final String TAG = "Pending";
    String routeId;
    final ParseUser me = ParseUser.getCurrentUser();
    private List<PassengerRouteClass> PassengerRouteClasslist = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_pending);
        routeId = getIntent().getStringExtra("routeId");
        //Log.e(TAG, "Found id: " + routeId);
        PassengerRouteClasslist = new ArrayList<PassengerRouteClass>();
        try {
            // Locate the class table named "Country" in Parse.com
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                    "ParseRoute");
            query.include("user");
            // by ascending
            query.orderByAscending("createdAt");
            ob = query.find();
            for (ParseObject route : ob) {
                //get array of pending
                //Log.e(TAG, route.getObjectId().toString() );
                if(route.getObjectId().toString().equals(routeId) && route.getList("bookers")!=null){
                    //Log.e(TAG, "Matched id");
                    List<String> ary = route.getList("bookers");
                    //Log.e(TAG, "bookers" + ary.toString());
                    if(!ary.isEmpty())
                        for(String req : ary){
                            PassengerRouteClass map = new PassengerRouteClass();
                            map.setBooker(req);
                            map.setDepDay((String) route.get("depDay"));
                            map.setDepTime((String) route.get("depTime"));
                            map.setFrom((String) route.get("from"));
                            map.setNumPass((String) route.get("numPass"));
                            map.setTo((String) route.get("to"));
                            map.setRouteId((String) route.getObjectId());
                            map.setCreatedAt(route.getCreatedAt().toString());
                            map.setUpdatedAt(route.getUpdatedAt().toString());
                            PassengerRouteClasslist.add(map);
                        }

                }
            }
        } catch (ParseException e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        listview = (ListView) findViewById(android.R.id.list);
        // Pass the results into ListViewAdapter.java
//            adapter = new ListViewAdapter(PassengerActivityListTab.this,
//                    PassengerRouteClasslist);
        adapter = new DriverPendingAdapter(getApplicationContext(),
                PassengerRouteClasslist);
        // Binds the Adapter to the ListView
        listview.setAdapter(adapter);
        // Close the progressdialog
        adapter.refresh();
        adapter.notifyDataSetChanged();
    }

}
