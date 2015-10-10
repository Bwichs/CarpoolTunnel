package carpooltunnel.slugging;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(ParseRoute.class);
        Parse.initialize(this, "VPZjlR7XLjBjn7KPCiCgaAexMUSX13fNe2wZ4mps", "lMHGifcMpdu9yKDL5YFT1Rw79fo466o7BngcneUF");
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(getApplicationContext());
    }
}