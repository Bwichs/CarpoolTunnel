package carpooltunnel.slugging;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v4.widget.SwipeRefreshLayout;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DriverRoutes extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    DriverListViewAdapter adapter;
    private List<PassengerRouteClass> PassengerRouteClasslist = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new RemoteDataTask().execute();
        return inflater.inflate(R.layout.activity_driver_activity_list_routes, container, false);
    }

    @Override
    public void onRefresh() {
        new RemoteDataTask().execute();
    }
    // RemoteDataTask AsyncTask
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("Your Routes");
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
                    map.setRouteId(route.getObjectId());
                    String name = n.getString("username");
                    /*try {
                        name = route.fetch().getString("ObjectId");
                    } catch (ParseException e){
                        e.printStackTrace();
                    }*/
                    map.setDriverUser(name);
                    map.setCreatedAt(route.getCreatedAt().toString());
                    map.setUpdatedAt(route.getUpdatedAt().toString());
                    //Log.e(TAG, "user:" + name + " " + ParseUser.getCurrentUser().getUsername());
                    //Log.e(TAG, "UA:" + route.getUpdatedAt().toString());
                    if (name.equals(ParseUser.getCurrentUser().getUsername())){
                        Log.e(TAG,"its in!"+name);
                    PassengerRouteClasslist.add(map);}
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
            adapter = new DriverListViewAdapter(getActivity(),
                    PassengerRouteClasslist);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            adapter.refresh();
            adapter.notifyDataSetChanged();
            //swipeRefresh.setRefreshing(false);
            mProgressDialog.dismiss();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
