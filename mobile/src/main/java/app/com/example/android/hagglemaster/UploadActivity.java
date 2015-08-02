package app.com.example.android.hagglemaster;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UploadActivity extends ActionBarActivity {
    private HaggleDB mHaggleDB;
    private SQLiteDatabase db;

    private static final String DATABASE_NAME = "item";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ADDR = "address";
    private static final String KEY_DESC = "description";
    private static final String KEY_IMG = "image";
    private static final String KEY_PRICE = "price";
    private static final String[] COLUMNS = {KEY_TITLE, KEY_PRICE, KEY_ADDR, KEY_DESC, KEY_IMG};
    private static final String TAG = "UploadActivityTAG";
    private String mCurrentPhotoPath = null;
    private Uri realPhoto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        mHaggleDB = new HaggleDB(getApplicationContext());
    }

    /** submit button clicked. need to upload shiza to the db, and then add a modal or
     * something to show that it was submitted */
    public void DBUpload(View view) {

        // somehow need to be able to get the image as well
        EditText title = (EditText) findViewById(R.id.title_text);
        EditText address = (EditText) findViewById(R.id.address_text);
        EditText description = (EditText) findViewById(R.id.description_text);
        EditText price = (EditText) findViewById(R.id.price_text);

        String titleText = title.getText().toString();
        String addressText = address.getText().toString();
        String descriptionText = description.getText().toString();
        double priceVal = Double.valueOf(price.getText().toString()).doubleValue();

        Log.d(TAG, titleText + " " + priceVal + " " + addressText + " " + descriptionText);

        db = mHaggleDB.getWritableDatabase();
        ContentValues vals = new ContentValues();
//        vals.put(KEY_IMG, null); // THIS IS JUST TEMPORARY FIX
        vals.put(KEY_TITLE, titleText);
        vals.put(KEY_PRICE, priceVal);
        vals.put(KEY_ADDR, addressText);
        vals.put(KEY_DESC, descriptionText);
        long newRowId = db.insert("item", null, vals);

        // clear all text
        title.setText("");
        address.setText("");
        price.setText("");
        description.setText("");

        // message sent to let user know updated
        Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show();
    }

    /** click to upload an image. need a camera intent and keep photo in this spot */
    public void cameraOpen(View view) {
        // fill in
        //first try
        Button btn = (Button) findViewById(R.id.imagebtn);
        btn.setVisibility(View.INVISIBLE);
        dispatchTakePictureIntent();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

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
        image.setImageURI(realPhoto);
        image.getLayoutParams().width = 1000;
        image.getLayoutParams().height = 500;
        image.setVisibility(View.VISIBLE);

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
}
