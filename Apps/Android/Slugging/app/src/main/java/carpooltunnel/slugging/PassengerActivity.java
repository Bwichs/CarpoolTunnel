package carpooltunnel.slugging;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.ParseUser;

public class PassengerActivity extends AppCompatActivity {
    FragmentManager fragmentManagerRoutes;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    public static final String TAG = "PA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
       // View header = LayoutInflater.from(this).inflate(R.layout.nav_header,null);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
       // nvDrawer.addHeaderView(header);

        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();



        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Route List"));
        tabLayout.addTab(tabLayout.newTab().setText("Route Map"));
        tabLayout.addTab(tabLayout.newTab().setText("My Routes"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        fragmentManagerRoutes = getSupportFragmentManager();
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setupDrawerContent(NavigationView navigationView) {

        //View header = LayoutInflater.from(this).inflate(R.layout.nav_header, null);
      //  navigationView.removeHeaderView(navigationView);
      //  navigationView.addHeaderView(navigationView);
        TextView etext = (TextView) navigationView.findViewById(R.id.nav_header_email);
        TextView ntext = (TextView) navigationView.findViewById(R.id.nav_header_name);

        if(etext != null && ntext != null){
            Log.e(TAG, "user:" + ParseUser.getCurrentUser().getUsername() + " name:" + ParseUser.getCurrentUser().get("name"));
            etext.setText(ParseUser.getCurrentUser().getUsername());
            ntext.setText(ParseUser.getCurrentUser().get("name").toString());
            // Sync the toggle state after onRestoreInstanceState has occurred.
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                startActivity(new Intent(this, DriverActivity.class));
                finish();
                break;
            case R.id.nav_second_fragment:
                startActivity(new Intent(this, PassengerActivity.class));
                finish();
                break;
            case R.id.nav_third_fragment:
                startActivity(new Intent(this, AccountActivity.class));
                finish();
                break;
            case R.id.nav_fourth_fragment:
                startActivity(new Intent(this, TutorialActivity.class));
                finish();
                break;
            case R.id.nav_fifth_fragment:
                new AlertDialog.Builder(PassengerActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Logout?")
                        .setMessage("Are you sure you wish to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ParseUser.logOut();
                                startActivity(new Intent(PassengerActivity.this, LoginActivity.class));
                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
            default:
                startActivity(new Intent(this, PassengerActivity.class));
        }
        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle()
    {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
        //respond to menu item selection
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        View header = LayoutInflater.from(this).inflate(R.layout.nav_header,null);
//        nvDrawer.addHeaderView(header);
//        TextView etext = (TextView) header.findViewById(R.id.nav_header_email);
//        TextView ntext = (TextView) header.findViewById(R.id.nav_header_name);
//        Log.e(TAG, "user:" + ParseUser.getCurrentUser().getUsername() + " name:" + ParseUser.getCurrentUser().get("name"));
//        if(etext != null && ntext != null){
//        etext.setText(ParseUser.getCurrentUser().getUsername());
//        ntext.setText(ParseUser.getCurrentUser().get("name").toString());
//        // Sync the toggle state after onRestoreInstanceState has occurred.
//        }
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }
}