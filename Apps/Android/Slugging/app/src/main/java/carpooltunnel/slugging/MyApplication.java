package carpooltunnel.slugging;

import android.app.Application;
import com.parse.Parse;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "VPZjlR7XLjBjn7KPCiCgaAexMUSX13fNe2wZ4mps", "lMHGifcMpdu9yKDL5YFT1Rw79fo466o7BngcneUF");
    }
}