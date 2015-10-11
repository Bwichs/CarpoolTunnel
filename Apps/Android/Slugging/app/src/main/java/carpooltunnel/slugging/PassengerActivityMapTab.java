package carpooltunnel.slugging;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class PassengerActivityMapTab extends Fragment {
    public static FragmentManager fragmentManagerStarting;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentManagerStarting = getFragmentManager();
        return inflater.inflate(R.layout.activity_passenger_activity_map_tab, container, false);
    }
}
