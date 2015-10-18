package carpooltunnel.slugging;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class DriverActivityMapFinishFragment extends Fragment implements OnMapReadyCallback {
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
        view = (RelativeLayout) inflater.inflate(R.layout.fragment_driver_activity_map_finish, container, false);
        // Passing harcoded values for latitude & longitude. Please change as per your need. This is just used to drop a Marker on the Map
        latitude = 36.9719;
        longitude = 122.0264;
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
        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Home").snippet("Home Address"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.9719, 122.0264), 16));
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

            ((MapFragment) this.getChildFragmentManager().findFragmentById(R.id.location_map_dest)).getMapAsync(this);
            if (mMap != null)
                onMapReady(mMap);
        }
    }

    /**** The mapfragment's id must be removed from the FragmentManager
     **** or else if the same it is passed on the next time then
     **** app will crash ****/
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (mMap != null) {
//            // PassengerActivity.fragmentManagerRoutes.beginTransaction()
//            //        .remove(PassengerActivity.fragmentManagerRoutes.findFragmentById(R.id.location_map_routes)).commit();
//            mMap = null;
//        }
//    }
}
