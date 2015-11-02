package carpooltunnel.slugging;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class DriverActivitySubmit extends Fragment {
    public final String TAG = "DriverActivity";

    public static FragmentManager fragmentManagerDest;
    public static FragmentManager fragmentManagerStarting;

    // UI references.
    private EditText mFrom;
    private EditText mTo;
    private EditText mNumPass;
    private EditText mTime;
    private EditText mDay;

    String from;
    String to;
    String numPass;
    String depTime;
    String depDay;
    ParseUser user;

    public void pushRouteSuccess(ParseUser driver, String from, String to, String date) {
        ParsePush push = new ParsePush();
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("user", driver);
        push.setQuery(pushQuery);
        push.setMessage("You have successfully submitted your route "
                + from + " to " + to + " on " + date + ".");
        push.sendInBackground();
    }

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = (RelativeLayout) inflater.inflate(R.layout.fragment_driver_activity_submit, container, false);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //this declares the map fragment, and hides them
        //fragmentManagerDest = new DriverActivityMapFinishFragment().getFragmentManager();
        //fragmentManagerStarting = new DriverActivityMapStartFragment().getFragmentManager();
        //Log.e(TAG, "fragstartest:" + fragmentManagerStarting);
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        //ft.hide(fragmentManagerStarting.findFragmentById(R.id.location_map_start));
        //ft.hide(fragmentManagerDest.findFragmentById(R.id.location_map_dest));
        //ft.commit();
        //function call for buttons to show/hide map fragments

        //addShowHideListener(R.id.startlocation, fragmentManagerStarting.findFragmentByTag("hello"));
        //addShowHideListener(R.id.destlocation, fragmentManagerDest.findFragmentById(R.id.location_map_dest));

        mFrom = (EditText) view.findViewById(R.id.start);
        mTo = (EditText) view.findViewById(R.id.finish);
        mNumPass = (EditText) view.findViewById(R.id.numpass);
        mTime = (EditText) view.findViewById(R.id.time);
        mDay = (EditText) view.findViewById(R.id.date);
        //time picker
        mTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mTime.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute));
                    }
                }, hour, minute, true);//24 Hour Time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

//        final Button startButton = (Button)findViewById(R.id.startlocation);
//        startButton.setOnClickListener(new View.OnClickListener(){
//        @Override
//        public void onClick(View v){
//
//        }
//        });

        //date picker
        mDay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Calendar myCalendar = Calendar.getInstance();
                int cDay = myCalendar.get(Calendar.DAY_OF_MONTH);
                int cDate = myCalendar.get(Calendar.DAY_OF_MONTH);
                int cMonth = myCalendar.get(Calendar.MONTH);
                int cYear = myCalendar.get(Calendar.YEAR);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedDate, int selectedMonth, int selectedYear) {
                        mDay.setText("" + selectedYear + "/" + (selectedMonth+1) + "/" + selectedDate);
                    }
                }, cYear, cMonth, cDate);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();
            }
        });

        Button mSubmitButton = (Button) view.findViewById(R.id.sched_sub_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRoute();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        return view;
    }
//    //button function
//    void addShowHideListener(int buttonId, final android.support.v4.app.Fragment fragment) {
//        Log.e(TAG,"hello"+fragment);
//        final Button button = (Button)getView().findViewById(buttonId);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Log.e(TAG,"onClick");
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.setCustomAnimations(android.R.animator.fade_in,
//                        android.R.animator.fade_out);
//                if (fragment.isHidden()) {
//                    ft.show(fragment);
//                    Log.e(TAG,"SHOW");
//
//                } else {
//                    ft.hide(fragment);
//                    Log.e(TAG, "HIDE");
//                }
//                ft.commit();
//            }
//        });
//    }




    private void submitRoute(){

        from = mFrom.getText().toString();
        to = mTo.getText().toString();
        numPass = mNumPass.getText().toString();
        depTime = mTime.getText().toString();
        depDay = mDay.getText().toString();
        if(!from.matches("") && !to.matches("") && !numPass.matches("") && !depTime.matches("") && !depDay.matches("")) {
            boolean locationFrom = false;
            boolean locationTo = false;

            List<Address> foundGeocodeFrom = null;
            List<Address> foundGeocodeTo = null;
            Geocoder geocoder;
            try{
                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                foundGeocodeFrom = geocoder.getFromLocationName(from, 1);
                if(foundGeocodeFrom != null && !foundGeocodeFrom.isEmpty()){
                    foundGeocodeFrom.get(0).getLatitude();
                    foundGeocodeFrom.get(0).getLongitude();
                    Log.e(TAG,"location from, latlong:"+foundGeocodeFrom.get(0).getLatitude()+" "+foundGeocodeFrom.get(0).getLongitude());
                    locationFrom = true;
                }
                else{
                    Log.e(TAG,"location not found");
                }

                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                foundGeocodeTo = geocoder.getFromLocationName(to, 1);
                if(foundGeocodeTo != null && !foundGeocodeTo.isEmpty()){
                    foundGeocodeTo.get(0).getLatitude();
                    foundGeocodeTo.get(0).getLongitude();
                    Log.e(TAG, "location to, latlong:" + foundGeocodeTo.get(0).getLatitude() + " " + foundGeocodeTo.get(0).getLongitude());
                    locationTo = true;
                }
                else{
                    Log.e(TAG,"location not found");
                }
            }
            catch (IOException ioexception){
                Log.e(TAG, "location error",ioexception);
            }
            if(locationFrom == true && locationTo == true) {
                ParseRoute route = new ParseRoute();
                user = ParseUser.getCurrentUser();
                route.setFrom(from);
                route.setTo(to);
                route.setNumPass(numPass);
                route.setDepTime(depTime);
                route.setDepDay(depDay);
                route.setUser(user);
                route.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Route added from " + from + " to " + to + " at " + depTime + " on " + depDay,
                                Toast.LENGTH_LONG).show();
                        //finish();
                    }
                });
                pushRouteSuccess(user, from, to, depDay);
            }
            else if(locationFrom == false && locationTo == true){
                Toast.makeText(getActivity().getApplicationContext(),
                        "Please enter a valid start address",
                        Toast.LENGTH_LONG).show();
            }
            else if(locationFrom == true && locationTo == false){
                Toast.makeText(getActivity().getApplicationContext(),
                        "Please enter a valid destination address",
                        Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getActivity().getApplicationContext(),
                        "Please enter valid start and destination addresses",
                        Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getActivity().getApplicationContext(),
                    "Please fill in the entire form",
                    Toast.LENGTH_LONG).show();
        }
    }


}