package carpooltunnel.slugging;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class PassengerActivityMapTab extends Fragment implements OnMapReadyCallback{
    public static final String TAG = "map";
    private static View view;
    /**
     * Note that this may be null if the Google Play services APK is not
     * available.
     */
    boolean canBook = true;;
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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng coord) {
                //Log.d(TAG, "Coordinates of press: " + coord.latitude + "-" + coord.longitude);

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng coord = marker.getPosition();
                //Log.d(TAG, "Coordinates of press: " + coord.latitude + "-" + coord.longitude);
                routeFromClick(coord);
                return false;
            }
        });
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.9719, -122.0264), 12));
        new RemoteDataTask().execute();
    }

    public void routeFromClick (LatLng coord){
        List<Address> foundGeocodeFrom = null;
        List<Address> foundGeocodeTo = null;
        String from = null;
        String to = null;
        final String routeDriver;
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        final ParseUser me = ParseUser.getCurrentUser();
        try {
            /*address = geocoder.getFromLocation(coord.latitude,coord.longitude, 1);
            for(int i = 0; i < address.get(0).getMaxAddressLineIndex(); i++) {
                if(i!=0) ad+= ", ";
                ad += address.get(0).getAddressLine(i).toString();
                //Log.e(TAG, address.get(0).getAddressLine(i).toString());
            }
            Log.e(TAG, "address = " + ad);

            }catch (IOException ioexception){
                Log.e(TAG, "address error",ioexception);
            }*/
            // Locate the class table named "Country" in Parse.com
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                    "ParseRoute");
            // by ascending
            query.orderByAscending("createdAt");
            query.include("user");
            ob = query.find();
            //Log.e(TAG, "coord, latlong:" + coord.latitude + ", " + coord.longitude);
            for (final ParseObject route : ob) {
                try{
                    from = (String) route.get("from");
                    foundGeocodeFrom = geocoder.getFromLocationName(from, 1);
                    to = (String) route.get("to");
                    foundGeocodeTo = geocoder.getFromLocationName(to, 1);
                    //Log.e(TAG, "location to, latlong:" + foundGeocodeTo.get(0).getLatitude() + ", " + foundGeocodeTo.get(0).getLongitude());
                }catch (IOException ioexception){
                    Log.e(TAG, "address error",ioexception);
                }

                if(route.getList("bookers")!=null){
                    List<String> ary = route.getList("bookers");
                    //Log.e(TAG, "bookers" + ary.toString());
                    if(ary.contains(me.getUsername().toString())){
                        canBook = false;
                    }
                }
                Location point = new Location("point");

                point.setLatitude(coord.latitude);
                point.setLongitude(coord.longitude);

                Location coordTo = new Location("coordTo");

                coordTo.setLatitude(foundGeocodeTo.get(0).getLatitude());
                coordTo.setLongitude(foundGeocodeTo.get(0).getLongitude());

                Location coordFrom = new Location("coordFrom");

                coordFrom.setLatitude(foundGeocodeFrom.get(0).getLatitude());
                coordFrom.setLongitude(foundGeocodeFrom.get(0).getLongitude());

                float distance1 = coordTo.distanceTo(point);
                float distance2 = coordFrom.distanceTo(point);

                //Log.e(TAG, "Distances: " + distance1 + ", " + distance2);
                if( distance1 < .1 || distance2 < .1){

                    final ParseObject n = route.getParseObject("user");
                    Log.e(TAG, "got route by " + n.getString("username"));
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Sign up for route?")
                            .setMessage("Do you want to book this route?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (((Integer.parseInt(route.getString("numPass")) - 1) >= 0) && !me.getUsername().equals(n.getString("username")) && canBook) {
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                "Successfully booked route!",
                                                Toast.LENGTH_LONG).show();
                                            int x = Integer.parseInt(route.getString("numPass")) - 1;
                                            route.add("bookers", me.getUsername().toString());
                                            route.put("numPass", String.valueOf(x));
                                            route.saveInBackground();
                                            canBook = false;
                                    }else{
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                "Error, you may already have booked this route or there is no room!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            }
        } catch (ParseException e) {
            Log.e("Error", e.getMessage());
        }
    }

    public void drawFromMap (Double fromLat, Double fromLong, Double toLat, Double toLong, String from, String to)
    {
        if(fromLat != null && fromLong != null) mMap.addMarker(new MarkerOptions().position(new LatLng(fromLat, fromLong)).title("From").snippet(from));
        if(toLat != null && toLat != null) mMap.addMarker(new MarkerOptions().position(new LatLng(toLat, toLong)).title("To").snippet(to)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            //Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
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
        public String from, to;

        public mapcoords(Double d1, Double d2, Double d3, Double d4, String fromd, String tod){
            super();
            fromlat = d1;
            fromlong = d2;
            tolat = d3;
            tolong = d4;
            from = fromd;
            to = tod;
        }
    }

    List<ParseObject> ob;
    private ArrayList<mapcoords> mapLatitude = new ArrayList<>();
    ProgressDialog mProgressDialog;

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
                            mapcoords data = new mapcoords(addressesFrom.get(0).getLatitude(),addressesFrom.get(0).getLongitude(),addressesTo.get(0).getLatitude(),addressesTo.get(0).getLongitude(), mapFrom, mapTo);
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
                if(member.fromlat != null && member.fromlong != null && member.tolat != null && member.tolong != null) drawFromMap(member.fromlat,member.fromlong,member.tolat,member.tolong, member.from, member.to);
                String url = getDirectionsUrl(new LatLng(member.fromlat,member.fromlong),new LatLng(member.tolat,member.tolong));
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
            }

        }
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(4);
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                lineOptions.color(color);
            }
            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
            mProgressDialog.dismiss();
        }
    }

    /**** The mapfragment's id must be removed from the FragmentManager
     **** or else if the same it is passed on the next time then
     **** app will crash ****/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
//            getChildFragmentManager().beginTransaction().
//                    remove(getChildFragmentManager().findFragmentById(R.id.location_maps_routes)).commit();
            mMap = null;
        }
    }

}
