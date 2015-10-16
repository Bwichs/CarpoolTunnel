package carpooltunnel.slugging;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import android.database.Cursor;

import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import android.util.Log;
import android.app.AlertDialog;
import android.widget.Toast;
import android.content.DialogInterface;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class AccountActivity extends AppCompatActivity {
    public static final String TAG = "map";
    public static int RESULT_LOAD_IMAGE = 1;
    final ParseUser user = ParseUser.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ParseFile carpic = (ParseFile) user.get("carpic");
        if(carpic != null) {
            carpic.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        // data has the bytes for the resume
                        Log.e("TAG", "car pic data loaded");
                        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                        ImageView image = (ImageView) findViewById(R.id.image);
                        image.setImageBitmap(Bitmap.createScaledBitmap(bm, 2048, 2048, false));
                    } else {
                        // something went wrong
                        Log.e("TAG", "no car pic data loaded");
                    }
                }
            });
        }
        TextView un = (TextView) findViewById(R.id.anContent);
        un.setText(user.getUsername());
        final EditText pn = (EditText) findViewById(R.id.pnContent);
        pn.setText(user.getString("phoneNumber"));
        final EditText ct = (EditText) findViewById(R.id.ctContent);
        ct.setText(user.getString("carType"));

        final Button submitButton = (Button) findViewById(R.id.sbutton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String phoneNumber = pn.getText().toString();
                user.put("phoneNumber", phoneNumber);
                pn.setText(phoneNumber);
                String carType = ct.getText().toString();
                user.put("carType", carType);
                ct.setText(carType);
                user.saveInBackground();
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
                                finish();
                                Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                                startActivity(intent);
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
               image.setImageBitmap(Bitmap.createScaledBitmap(bm, 2048, 2048, false ));
            }
            catch(FileNotFoundException e){
                Log.e(TAG, "file not found");
            }


            //try to save image as byte[] to store in parse database
            File imagefile = new File(picturePath);
            int size = (int) imagefile.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(imagefile));
                buf.read(bytes, 0, bytes.length);
                buf.close();
                ParseFile file = new ParseFile("accountpic.jpg", bytes);
                file.saveInBackground();
                user.put("carpic", file);
                user.saveInBackground();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
