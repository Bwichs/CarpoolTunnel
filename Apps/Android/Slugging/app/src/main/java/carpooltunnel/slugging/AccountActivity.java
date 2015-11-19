package carpooltunnel.slugging;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.List;


public class AccountActivity extends AppCompatActivity {
    public static final String TAG = "map";
    public static int RESULT_LOAD_IMAGE = 1;
    ParseUser user = ParseUser.getCurrentUser();
    public boolean picSaved;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();
        final EditText name = (EditText) findViewById(R.id.nameContent);
        final EditText pn = (EditText) findViewById(R.id.pnContent);
        final EditText ct = (EditText) findViewById(R.id.ctContent);
        final Button passwordButton = (Button) findViewById(R.id.pwbutton);
        final Button fab = (Button) findViewById(R.id.fab);
        final Button deleteButton = (Button) findViewById(R.id.dButton);
        final TextView un = (TextView) findViewById(R.id.anContent);

        TextView navname = (TextView) findViewById(R.id.nav_header_name);
        //navname.setText(user.getString("name"));
        TextView navemail = (TextView) findViewById(R.id.nav_header_email);
        //navemail.setText(user.getUsername());

        if(getIntent().getStringExtra("email") != null){

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("email", getIntent().getStringExtra("email"));
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> list, ParseException e) {
                    user = list.get(0);
                    Log.e(TAG, user.getUsername());
                    ParseFile carpic = user.getParseFile("carpic");
                    carpic.getDataInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                // data has the bytes
                                Log.e("TAG", "car pic data loaded for " + user.getUsername());
                                Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                                ImageView image = (ImageView) findViewById(R.id.image);
                                image.setImageBitmap(Bitmap.createScaledBitmap(bm, 640, 512, false));
                            } else {
                                // something went wrong
                                Log.e("TAG", "no car pic data loaded");
                            }
                        }
                    });

                    un.setText(user.getUsername());
                    ct.setText(user.getString("carType"));
                    pn.setText(user.getString("phoneNumber"));
                    name.setText(user.getString("name"));
                    name.setEnabled(false);
                    name.setHint("");
                    pn.setEnabled(false);
                    pn.setHint("");
                    ct.setEnabled(false);
                    ct.setHint("");
                    passwordButton.setVisibility(View.INVISIBLE);
                    passwordButton.setEnabled(false);
                    fab.setVisibility(View.INVISIBLE);
                    fab.setEnabled(false);
                    deleteButton.setVisibility(View.INVISIBLE);
                    deleteButton.setEnabled(false);
                }
            });
        }else {
            un.setText(user.getUsername());
            ct.setText(user.getString("carType"));
            pn.setText(user.getString("phoneNumber"));
            name.setText(user.getString("name"));
            ParseFile carpic = user.getParseFile("carpic");
            if (carpic != null) {
                carpic.getDataInBackground(new GetDataCallback() {
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            // data has the bytes
                            Log.e("TAG", "car pic data loaded for " + user.getUsername());
                            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                            ImageView image = (ImageView) findViewById(R.id.image);
                            image.setImageBitmap(Bitmap.createScaledBitmap(bm, 640, 512, false));
                        } else {
                            // something went wrong
                            Log.e("TAG", "no car pic data loaded");
                        }
                    }
                });
            } else {
                Log.e("TAG", "car pic null");
            }
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = pn.getText().toString();
                pn.setText(phoneNumber);
                String carType = ct.getText().toString();
                ct.setText(carType);
                String n = name.getText().toString();
                user.put("name", n);
                user.put("carType", carType);
                user.put("phoneNumber", phoneNumber);
                Intent intent = new Intent(AccountActivity.this, PassengerActivity.class);
                SaveCallback sc = new SaveCallback() {
                    @Override
                    public void done(ParseException arg0) {
                        Log.e("TAG", "after saveinbackground is done");
                        if(picSaved) finish();
                    }
                };
                user.saveInBackground(sc);
                startActivity(intent);
            }
        });


        passwordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ParseUser.requestPasswordResetInBackground(user.getEmail(), new RequestPasswordResetCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.e(TAG, "Sent email reset");
                            Toast.makeText(getApplicationContext(),
                                    "Successfully sent password reset email!",
                                    Toast.LENGTH_LONG).show();
                            // An email was successfully sent with reset instructions.
                        } else {
                            // Something went wrong. Look at the ParseException to see what's up.
                        }
                    }
                });
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(AccountActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete your account?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Integer.valueOf(android.os.Build.VERSION.SDK) >= 21) {
                                    user.deleteInBackground();
                                    Log.e(TAG, ">21: ");
                                } else {
                                    user.deleteEventually();
                                    Log.e(TAG, "<21: ");
                                }
                                Toast.makeText(getApplicationContext(),
                                        "Successfully deleted Account!",
                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        final ImageView image = (ImageView) findViewById(R.id.image);
        if(getIntent().getStringExtra("email") == null) {
            image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(
                            Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, RESULT_LOAD_IMAGE);

                }
            });
        }
        /*facebookButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                    final List<String> permissions = Arrays.asList("public_profile");
                    ParseFacebookUtils.linkWithReadPermissionsInBackground(ParseUser.getCurrentUser(), AccountActivity.this, permissions, new SaveCallback() {
                        @Override
                        public void done(ParseException ex) {
                            if (ParseFacebookUtils.isLinked(user)) {
                                Log.e(TAG, "Linked");
                            }
                        }
                    });
                }
                else
                {
                    Log.e(TAG,"Pressed");
                    if (ParseFacebookUtils.isLinked(user)) {
                        Log.e(TAG,"Logged in");
                        facebookButton.setText("Go to Account");
                    }
                }
            }
        });*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            Log.e(TAG, picturePath);
            cursor.close();

            final ImageView image = (ImageView) findViewById(R.id.image);
            //File img = new File(picturePath);
            Bitmap bm;
            try{
               bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
               Bitmap bmap = Bitmap.createScaledBitmap(bm, 640, 512, false);
                Log.e(TAG, "test");
                image.setImageBitmap(bmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmap.compress(Bitmap.CompressFormat.PNG, 30, stream);
                byte[] byteArray = stream.toByteArray();
                final ParseFile file = new ParseFile("accountpic.jpg", byteArray);
                SaveCallback sc = new SaveCallback() {
                    @Override
                    public void done(ParseException arg0) {
                        user.put("carpic", file);
                        SaveCallback sc2 = new SaveCallback() {
                            @Override
                            public void done(ParseException arg0) {
                                Toast.makeText(getApplicationContext(),
                                        "Picture Saved!",
                                        Toast.LENGTH_LONG).show();
                                picSaved = true;
                            }
                        };
                        user.saveInBackground(sc2);
                    }
                };
                file.saveInBackground(sc);
                Toast.makeText(getApplicationContext(),
                        "Saving... Please Wait",
                        Toast.LENGTH_LONG).show();


            }
            catch(FileNotFoundException e){
                Log.e(TAG, "file not found");
            }

        }
    }
    private void setupDrawerContent(NavigationView navigationView) {
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
                new AlertDialog.Builder(AccountActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Logout?")
                        .setMessage("Are you sure you wish to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ParseUser.logOut();
                                startActivity(new Intent(AccountActivity.this, LoginActivity.class));
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
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }
}
