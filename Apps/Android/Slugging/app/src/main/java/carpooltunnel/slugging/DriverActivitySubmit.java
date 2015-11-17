package carpooltunnel.slugging;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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
import java.util.Random;


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
                new AlertDialog.Builder(DriverActivitySubmit.this.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Submit?")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                submitRoute();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        return view;
    }

    private void submitRoute(){

        from = mFrom.getText().toString();
        to = mTo.getText().toString();
        numPass = mNumPass.getText().toString();
        depTime = mTime.getText().toString();
        depDay = mDay.getText().toString();
        if(!from.matches("") && !to.matches("") && !numPass.matches("") && !depTime.matches("") && !depDay.matches("")) {
            boolean locationFrom = false;
            boolean locationTo = false;
            boolean locationSame = false;

            List<Address> foundGeocodeFrom = null;
            List<Address> foundGeocodeTo = null;
            Geocoder geocoder;
            try{
                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                foundGeocodeFrom = geocoder.getFromLocationName(from, 1);
                //radius of 50 miles ~ 80467.2 metres from SC
                if(foundGeocodeFrom != null && !foundGeocodeFrom.isEmpty()){
                    foundGeocodeFrom.get(0).getLatitude();
                    foundGeocodeFrom.get(0).getLongitude();
                    Log.e(TAG,"location from, latlong:"+foundGeocodeFrom.get(0).getLatitude()+" "+foundGeocodeFrom.get(0).getLongitude());
                    float[] distance = new float[3];
                    Location.distanceBetween(36.9719,-122.0264,foundGeocodeFrom.get(0).getLatitude(),foundGeocodeFrom.get(0).getLongitude(),distance );
                    if(distance[0] <= 80500)
                        locationFrom = true;
                    else{
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Please enter an address within 50 miles of Santa Cruz",
                                Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Log.e(TAG,"From location not found");
                }

                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                foundGeocodeTo = geocoder.getFromLocationName(to, 1);
                //radius of 50 miles ~ 80467.2 metres from SC
                if(foundGeocodeTo != null && !foundGeocodeTo.isEmpty()){
                    foundGeocodeTo.get(0).getLatitude();
                    foundGeocodeTo.get(0).getLongitude();
                    float[] distance = new float[3];
                    Location.distanceBetween(36.9719,-122.0264,foundGeocodeTo.get(0).getLatitude(),foundGeocodeTo.get(0).getLongitude(),distance );
                    Log.e(TAG, "location to, latlong:" + foundGeocodeTo.get(0).getLatitude() + " " + foundGeocodeTo.get(0).getLongitude()+" distbwtTo:"+distance[0]+" "+distance[1]+" "+distance[2]);
                    if(distance[0] <= 80500)
                    locationTo = true;
                    else{
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Please enter an address within a 50 mile radius of Santa Cruz",
                                Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Log.e(TAG,"To location not found");
                }
                if(foundGeocodeTo.get(0).getLatitude() == foundGeocodeFrom.get(0).getLatitude() && foundGeocodeTo.get(0).getLongitude() == foundGeocodeFrom.get(0).getLongitude()) {
                    locationSame = true;
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Start and Finish cannot be the same location!",
                            Toast.LENGTH_LONG).show();
                }
            }
            catch (IOException ioexception){
                Log.e(TAG, "location error",ioexception);
            }
            if(locationFrom == true && locationTo == true && locationSame == false) {
                ParseRoute route = new ParseRoute();

                to = to.substring(0,1).toUpperCase() + to.substring(1).toLowerCase();
                from = from.substring(0,1).toUpperCase() + from.substring(1).toLowerCase();

                Random rnd = new Random();
                int colour = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                user = ParseUser.getCurrentUser();
                route.setFrom(from);
                route.setTo(to);
                route.setNumPass(numPass);
                route.setDepTime(depTime);
                route.setDepDay(depDay);
                route.setUser(user);
                route.setColour(colour);
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
            else if(locationFrom == false && locationTo == false){
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
