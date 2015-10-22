package carpooltunnel.slugging;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
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
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class PassengerMyRoutes extends ListFragment {

    ListView listview;
    List<ParseObject> ob;
    ProgressDialog mProgressDialog;
    ListViewAdapter adapter;
    final List<String> bookers = new ArrayList<String>();

    final ParseUser me = ParseUser.getCurrentUser();
    private List<PassengerRouteClass> PassengerRouteClasslist = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        new RemoteDataTask().execute();
        return inflater.inflate(R.layout.fragment_passenger_my_routes, container, false);
    }

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(getActivity());
            // Set progressdialog title
            mProgressDialog.setTitle("Booked Routes");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }
        public final String TAG = "myBookedRoutes";
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
                    if(route.getList("bookers")!=null){
                        List<String> ary = route.getList("bookers");
                        //Log.e(TAG, "bookers" + ary.toString());
                        if(ary.contains(me.getUsername().toString())){

                        }
                    }
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
