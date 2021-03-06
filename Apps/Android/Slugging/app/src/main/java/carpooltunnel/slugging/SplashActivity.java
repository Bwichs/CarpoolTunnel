package carpooltunnel.slugging;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class SplashActivity extends Activity {

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1700;
    final static String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= 23) {
            int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < 23) {

            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity


                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    Log.i(TAG, "should intent here too");
                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }

    }
        @Override
        public void onRequestPermissionsResult ( int requestCode,
        String permissions[], int[] grantResults){
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        Log.i(TAG, "should intent here too");
                        // close this activity
                        finish();

                    } else {

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        finish();
                    }
                    return;
                }

                // other 'case' lines to check for other
                // permissions this app might request
            }
        }
    }
