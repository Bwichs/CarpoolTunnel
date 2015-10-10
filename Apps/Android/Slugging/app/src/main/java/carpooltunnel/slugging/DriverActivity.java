package carpooltunnel.slugging;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;

public class DriverActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mFrom = (EditText) findViewById(R.id.start);
        mTo = (EditText) findViewById(R.id.finish);
        mNumPass = (EditText) findViewById(R.id.numpass);
        mTime = (EditText) findViewById(R.id.time);
        mDay = (EditText) findViewById(R.id.date);

        mTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(DriverActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        mTime.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute));
                    }
                }, hour, minute, true);//24 Hour Time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        Button mSubmitButton = (Button) findViewById(R.id.sched_sub_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRoute();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public final String TAG = "DriverActivity";

    private void submitRoute(){

        from = mFrom.getText().toString();
        to = mTo.getText().toString();
        numPass = mNumPass.getText().toString();
        depTime = mTime.getText().toString();
        depDay = mDay.getText().toString();
        if(!from.matches("") && !to.matches("") && !numPass.matches("") && !depTime.matches("") && !depDay.matches("")) {
             Log.e(TAG, "true: Route added from " + from + " to " + to + " at " + depTime + " on " + depDay);
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
                     Toast.makeText(getApplicationContext(),
                             "Route added from " + from + " to " + to + " at " + depTime + " on " + depDay,
                             Toast.LENGTH_LONG).show();
                     finish();
                 }
             });
         }
        else{
            Log.e(TAG,"false: Route added from " + from + " to " + to + " at " + depTime + " on " + depDay);
            Toast.makeText(getApplicationContext(),
                    "Please fill in the entire form",
                    Toast.LENGTH_LONG).show();
        }
    }

}


