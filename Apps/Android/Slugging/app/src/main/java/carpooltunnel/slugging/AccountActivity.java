package carpooltunnel.slugging;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;



public class AccountActivity extends AppCompatActivity {
    public static final String TAG = "map";
    public static int RESULT_LOAD_IMAGE = 1;
    final ParseUser user = ParseUser.getCurrentUser();
    public boolean picSaved;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        final EditText name = (EditText) findViewById(R.id.nameContent);
        name.setText(user.getString("name"));
        TextView un = (TextView) findViewById(R.id.anContent);
        un.setText(user.getUsername());
        final EditText pn = (EditText) findViewById(R.id.pnContent);
        pn.setText(user.getString("phoneNumber"));
        final EditText ct = (EditText) findViewById(R.id.ctContent);
        ct.setText(user.getString("carType"));

        ParseFile carpic = user.getParseFile("carpic");
        if(carpic != null) {
            carpic.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        // data has the bytes
                        Log.e("TAG", "car pic data loaded");
                        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                        ImageView image = (ImageView) findViewById(R.id.image);
                        image.setImageBitmap(Bitmap.createScaledBitmap(bm, 640, 512, false));
                    } else {
                        // something went wrong
                        Log.e("TAG", "no car pic data loaded");
                    }
                }
            });
        }else{Log.e("TAG", "car pic null");}

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
                Intent intent = new Intent(AccountActivity.this, WelcomeActivity.class);
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

        final Button passwordButton = (Button) findViewById(R.id.pwbutton);
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

        final Button deleteButton = (Button) findViewById(R.id.dButton);
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
        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View  v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);

            }
        });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.backbtn) {

            Intent intent = new Intent(AccountActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
        //respond to menu item selection
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_backbtn, menu);
        return true;
    }
}
