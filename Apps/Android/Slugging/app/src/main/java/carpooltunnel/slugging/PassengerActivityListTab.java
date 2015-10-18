package carpooltunnel.slugging;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PassengerActivityListTab extends ListFragment {

    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    ListViewAdapter adapter;
    private List<PassengerRouteClass> PassengerRouteClasslist = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new RemoteDataTask().execute();
        return inflater.inflate(R.layout.activity_passenger_activity_list_tab, container, false);
    }


    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("Current Routes");
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
                query.include("user");
                // by ascending
                query.orderByAscending("createdAt");
                ob = query.find();
                for (ParseObject route : ob) {
                    ParseObject n = new ParseObject("User");
                    n = route.getParseObject("user");
                    PassengerRouteClass map = new PassengerRouteClass();
                    map.setDepDay((String) route.get("depDay"));
                    map.setDepTime((String) route.get("depTime"));
                    map.setFrom((String) route.get("from"));
                    map.setNumPass((String) route.get("numPass"));
                    map.setTo((String) route.get("to"));
                    String name = n.getString("username");
                    /*try {
                        name = route.fetch().getString("ObjectId");
                    } catch (ParseException e){
                        e.printStackTrace();
                    }*/
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
            listview = (ListView) getView().findViewById(android.R.id.list);
            // Pass the results into ListViewAdapter.java
//            adapter = new ListViewAdapter(PassengerActivityListTab.this,
//                    PassengerRouteClasslist);
            adapter = new ListViewAdapter(getActivity(),
                    PassengerRouteClasslist);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
