package carpooltunnel.slugging;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class DriverPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public DriverPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                DriverActivitySubmit tab1 = new DriverActivitySubmit();
                return tab1;
            case 1:
                DriverRoutes tab2 = new DriverRoutes();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}