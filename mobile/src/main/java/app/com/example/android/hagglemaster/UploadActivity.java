package app.com.example.android.hagglemaster;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;

import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String DATABASE_NAME = "item";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESC = "description";
    private static final String KEY_IMG = "image";
    private static final String KEY_PRICE = "price";
    private static final String KEY_DATE = "date";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";
    private static final String KEY_RATING = "rating";

    private static final String TAG = UploadActivity.class.getSimpleName();
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private HaggleDB mHaggleDB;
    private SQLiteDatabase db;
    private String mCurrentPhotoPath = null;
    private Uri realPhoto = null;
    private byte[] img = null;

    GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        fontText();

        mHaggleDB = new HaggleDB(getApplicationContext());

        addListenerOnRatingBar();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

    }

    float numStars = 0;
    /** submit button clicked. need to upload shiza to the db */
    public void DBUpload(View view) {

        EditText title = (EditText) findViewById(R.id.title_text);
        EditText description = (EditText) findViewById(R.id.description_text);
        EditText price = (EditText) findViewById(R.id.price_text);
        ImageView imgView = (ImageView) findViewById(R.id.imageView1);
        Button imgbut = (Button) findViewById(R.id.imagebtn);
        RatingBar rate = (RatingBar) findViewById(R.id.ratingBar);

        // bitmap stuff to put image in db
        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver() , realPhoto);
//            Bitmap bitmap = Bitmap.createScaledBitmap(bm, dptopx(120), dptopx(120), true);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);

            img = bos.toByteArray();
        }
        catch (Exception e)
        {
            // handle exception
            Log.e(TAG, "error handling bitmap");
        }

        String titleText = title.getText().toString().toLowerCase().trim();
        String descriptionText = description.getText().toString();
//        final float numStars = 0;


        //float numStars = rate.getRating();

        if (titleText.matches("") || descriptionText.matches("") || price.getText().toString().matches("")) {
            Toast t = new Toast(getApplicationContext());
            t.setGravity(Gravity.START, 100, 100); //TODO: get this to work
            t.makeText(this, "Please fill in\nall text fields!", Toast.LENGTH_SHORT).show();
        } else {
            double priceVal = Double.valueOf(price.getText().toString());
            ContentValues vals = new ContentValues();
            SimpleDateFormat sf = new SimpleDateFormat("MMM d, yyyy");
            String todayDate = sf.format(new Date());

            db = mHaggleDB.getWritableDatabase();
            vals.put(KEY_TITLE, titleText);
            vals.put(KEY_PRICE, priceVal);
            vals.put(KEY_DESC, descriptionText);
            vals.put(KEY_IMG, img);
            vals.put(KEY_RATING, numStars);
            vals.put(KEY_DATE, todayDate);
            vals.put(KEY_LAT, currentLatitude);
            vals.put(KEY_LONG, currentLongitude);
            db.insert(DATABASE_NAME, null, vals);

            // message sent to let user know updated
            Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show();

            // clear all text and remove image
            title.setText("");
            price.setText("");
            description.setText("");
            rate.setRating(0);
            imgView.setVisibility(View.INVISIBLE);
            imgbut.setVisibility(View.VISIBLE);

            Intent i = new Intent(UploadActivity.this, Redirect.class);
            startActivity(i);
        }
    }
    //rating bar
    public void addListenerOnRatingBar() {

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                numStars = ratingBar.getRating();
            }
        });
    }


    /** click to upload an image. need a camera intent and keep photo in this spot */
    public void cameraOpen(View view) {
        new Thread(new Runnable() {
            public void run() {
                dispatchTakePictureIntent();
    }}).start();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d(TAG, "ERROR");
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                realPhoto = Uri.fromFile(photoFile);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView image = (ImageView) findViewById(R.id.imageView1);
        Button btn = (Button) findViewById(R.id.imagebtn);
        try {
            ExifInterface exif = new ExifInterface(realPhoto.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            Matrix matrix = new Matrix();
            if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), realPhoto);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            OutputStream os = getContentResolver().openOutputStream(realPhoto);
            realPhoto = getImageUri(getApplicationContext(), bitmap, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        image.setImageURI(realPhoto);
        image.getLayoutParams().width = dptopx(300);
        image.getLayoutParams().height = dptopx(500);
        btn.setVisibility(View.INVISIBLE);
        image.setVisibility(View.VISIBLE);
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage, OutputStream os) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, os);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = "file:" + "/" + image.getAbsolutePath();
        mCurrentPhotoPath = mCurrentPhotoPath.substring(0, mCurrentPhotoPath.length() - 4);
        Log.d(TAG, "image path is..." + mCurrentPhotoPath);
        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected. upload activity");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            onLocationChanged(location);
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // nada
        Log.e(TAG, "connectionSuspended, shiza");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        Log.d(TAG, "curlat: " + currentLatitude + " curlong: " + currentLongitude);
    }

    /** converts dp to pixels */
    private int dptopx(int px) {
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (px * scale + 0.5f);
        return padding_in_px;
    }

    /** set font for writing in layout */
    private void fontText() {
        TextView t = (TextView) findViewById(R.id.title);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Pacifico.ttf");
        t.setTypeface(type);
        TextView t1 = (TextView)findViewById(R.id.textView2);
        Typeface type1 = Typeface.createFromAsset(getAssets(),"fonts/Raleway-Italic.ttf");
        t1.setTypeface(type1);
        TextView t2 = (TextView)findViewById(R.id.textView);
        t2.setTypeface(type1);
        EditText t3 = (EditText)findViewById(R.id.title_text);
        t3.setTypeface(type1);
        EditText t4 = (EditText)findViewById(R.id.price_text);
        t4.setTypeface(type1);
        TextView t5 = (TextView)findViewById(R.id.textView4);
        t5.setTypeface(type1);
        TextView t6 = (TextView)findViewById(R.id.textView5);
        t6.setTypeface(type1);
        EditText t7 = (EditText)findViewById(R.id.description_text);
        t7.setTypeface(type1);
    }

}
