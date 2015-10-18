package carpooltunnel.slugging;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PassengerActivityMapTab extends Fragment implements OnMapReadyCallback{
    public static final String TAG = "map";
    private static View view;
    /**
     * Note that this may be null if the Google Play services APK is not
     * available.
     */

    private static GoogleMap mMap;
    private static Double latitude, longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        view = (RelativeLayout) inflater.inflate(R.layout.activity_passenger_activity_map_tab, container, false);
        // Passing harcoded values for latitude & longitude. Please change as per your need. This is just used to drop a Marker on the Map
        latitude = 36.9719;
        longitude = -122.0264;
        //return inflater.inflate(R.layout.activity_passenger_activity_map_tab, container, false);
        return view;
    }

    @Override
    public void onMapReady (GoogleMap googleMap){
        mMap = googleMap;
        googleMap.setMyLocationEnabled(true);
//        LatLng location = mMap.getMyLocation();
//        if (location != null) {
//           LatLng myLocation = new LatLng(location.getLatitude(),
//                    location.getLongitude());
//        }
        // For dropping a marker at a point on the Map
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Home").snippet("Home Address"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.9719, -122.0264), 12));
        new RemoteDataTask().execute();
    }

    public void drawFromMap (double fromLat, double fromLong, double toLat, double toLong)
    {
        mMap.addMarker(new MarkerOptions().position(new LatLng(fromLat, fromLong)).title("From").snippet("Home Address"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(toLat, toLong)).title("To").snippet("Home Address"));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // TODO Auto-generated method stub
        if (mMap != null)
            onMapReady(mMap);

        if (mMap == null) {
            Log.e(TAG, "maptest4:" + latitude);

//            SupportMapFragment mapFragment;
//            if (Build.VERSION.SDK_INT < 21) {
//                mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.location_maps_routes);
//            } else {
//                mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.location_maps_routes);
//            }

            ((SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.location_maps_routes)).getMapAsync(this);
            if (mMap != null)
                onMapReady(mMap);
        }
    }

    public class mapcoords{
        public Double fromlat, fromlong, tolat, tolong;

        public mapcoords(Double d1, Double d2, Double d3, Double d4){
            super();
            fromlat = d1;
            fromlong = d2;
            tolat = d3;
            tolong = d4;
        }
    }

    List<ParseObject> ob;
    private ArrayList<mapcoords> mapLatitude = new ArrayList<>();

    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
        }
        public final String TAG = "PA";
        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            try {
                // Locate the class table named "Country" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "ParseRoute");
                query.include("user");
                // by ascending
                query.orderByAscending("createdAt");
                ob = query.find();
                Geocoder geocoder = new Geocoder(getActivity().getApplicationContext());
                for (ParseObject route : ob) {
                    ParseObject n = new ParseObject("User");
                    n = route.getParseObject("user");
                    String depDay = (String) route.get("depDay");
                    String depTime = (String) route.get("depTime");
                    String mapFrom= (String) route.get("from");
                    String numPass = (String) route.get("numPass");
                    String mapTo = (String) route.get("to");
                    String name = n.getString("username");
                    try {
                        List<Address> addressesFrom = geocoder.getFromLocationName(mapFrom,1);
                        List<Address> addressesTo = geocoder.getFromLocationName(mapTo,1);
                        if( addressesFrom.size() > 0){
                            mapcoords data = new mapcoords(addressesFrom.get(0).getLatitude(),addressesFrom.get(0).getLongitude(),addressesTo.get(0).getLatitude(),addressesTo.get(0).getLongitude());
                            mapLatitude.add(data);
                        }
                    } catch (IOException e){
                        e.printStackTrace();
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
//            for(int i=0;i<mapLatitude.length;i++){
//            //Log.e(TAG,"postDraw:"+mapLatitude[i]+" "+mapLongitude[i]);
//                if(mapLatitude[i] != null) drawFromMap(mapLatitude[i],mapLongitude[i]);}
            for(mapcoords member: mapLatitude){
                drawFromMap(member.fromlat,member.fromlong,member.tolat,member.tolong);
            }
        }
    }

    /**** The mapfragment's id must be removed from the FragmentManager
     **** or else if the same it is passed on the next time then
     **** app will crash ****/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
            // PassengerActivity.fragmentManagerRoutes.beginTransaction()
            //        .remove(PassengerActivity.fragmentManagerRoutes.findFragmentById(R.id.location_map_routes)).commit();
            mMap = null;
        }
    }
}
