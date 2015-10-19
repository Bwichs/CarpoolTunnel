package carpooltunnel.slugging;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                PassengerActivityListTab tab1 = new PassengerActivityListTab();
                return tab1;
            case 1:
                PassengerActivityMapTab tab2 = new PassengerActivityMapTab();
                return tab2;
            case 2:
                PassengerMyRoutes tab3 = new PassengerMyRoutes();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}