package carpooltunnel.slugging;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationFragment extends Fragment {
    public static final String TAG = "map";
    private static View view;
    /**
     * Note that this may be null if the Google Play services APK is not
     * available.
     */

    private static GoogleMap mMap;
    private static Double latitude, longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        view = (RelativeLayout) inflater.inflate(R.layout.location_fragment, container, false);
        // Passing harcoded values for latitude & longitude. Please change as per your need. This is just used to drop a Marker on the Map
        latitude = 36.9719;
        longitude = 122.0264;

        setUpMapIfNeeded(); // For setting up the MapFragment

        return view;
    }

    /***** Sets up the map if it is possible to do so *****/
    public static void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            Log.e(TAG,"maptest1:"+latitude);
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) DriverActivity.fragmentManagerDest
                    .findFragmentById(R.id.location_mapDest)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null){
                Log.e(TAG,"maptest2:"+latitude);
                setUpMap();}
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the
     * camera.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap}
     * is not null.
     */

    private static void setUpMap() {
        // For showing a move to my loction button
        mMap.setMyLocationEnabled(true);
//        LatLng location = mMap.getMyLocation();
//        if (location != null) {
//           LatLng myLocation = new LatLng(location.getLatitude(),
//                    location.getLongitude());
//        }


        // For dropping a marker at a point on the Map
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Home").snippet("Home Address"));
        // For zooming automatically to the Dropped PIN Location
        latitude = 36.9719;
        longitude = 122.0264;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.9719,122.0264), 16));
        Log.e(TAG,"maptest3:"+latitude);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (mMap != null)
            setUpMap();

        if (mMap == null) {
            Log.e(TAG,"maptest4:"+latitude);
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) DriverActivity.fragmentManagerDest
                    .findFragmentById(R.id.location_mapDest)).getMap(); // getMap is deprecated
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }

    /**** The mapfragment's id must be removed from the FragmentManager
     **** or else if the same it is passed on the next time then
     **** app will crash ****/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
            DriverActivity.fragmentManagerDest.beginTransaction()
                    .remove(DriverActivity.fragmentManagerDest.findFragmentById(R.id.location_mapDest)).commit();
            mMap = null;
        }
    }
}