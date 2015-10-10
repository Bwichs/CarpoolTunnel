package carpooltunnel.slugging;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PassengerActivity extends AppCompatActivity {

    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    ListViewAdapter adapter;
    private List<PassengerRouteClass> PassengerRouteClasslist = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_routs);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();
    }

    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(PassengerActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Parse.com Custom ListView Tutorial");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }
        public final String TAG = "PA";
        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            PassengerRouteClasslist = new ArrayList<PassengerRouteClass>();
            try {
                // Locate the class table named "Country" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "ParseRoute");
                // by ascending
                query.orderByAscending("createdAt");
                ob = query.find();
                for (ParseObject route : ob) {
                    PassengerRouteClass map = new PassengerRouteClass();
                    map.setDepDay((String) route.get("depDay"));
                    map.setDepTime((String) route.get("depTime"));
                    map.setFrom((String) route.get("from"));
                    map.setNumPass((String) route.get("numPass"));
                    map.setTo((String) route.get("to"));
                    String name = "";
                    try {
                        name = route.fetch().getString("user");
                    } catch (ParseException e){
                        e.printStackTrace();
                    }
                    map.setDriverUser(name);
                    map.setCreatedAt(route.getCreatedAt().toString());
                    map.setUpdatedAt(route.getUpdatedAt().toString());
                    Log.e(TAG, "user:" + name);
                    Log.e(TAG, "UA:" + route.getUpdatedAt().toString());
                    PassengerRouteClasslist.add(map);
                }
            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            listview = (ListView) findViewById(R.id.listview);
            // Pass the results into ListViewAdapter.java
            adapter = new ListViewAdapter(PassengerActivity.this,
                    PassengerRouteClasslist);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }

}
