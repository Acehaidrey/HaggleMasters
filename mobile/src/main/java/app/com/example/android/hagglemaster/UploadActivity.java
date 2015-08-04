package app.com.example.android.hagglemaster;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Handler.*;
import java.util.logging.LogRecord;


public class UploadActivity extends Activity {

    private static final String DATABASE_NAME = "item";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ADDR = "address";
    private static final String KEY_DESC = "description";
    private static final String KEY_IMG = "image";
    private static final String KEY_PRICE = "price";
    private static final String[] COLUMNS = {KEY_TITLE, KEY_PRICE, KEY_ADDR, KEY_DESC, KEY_IMG};
    private static final String TAG = "UploadActivityTAG";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private HaggleDB mHaggleDB;
    private SQLiteDatabase db;
    private String mCurrentPhotoPath = null;
    private Uri realPhoto = null;
    private byte[] img = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        TextView t = (TextView) findViewById(R.id.title);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Pacifico.ttf");
        t.setTypeface(type);
        mHaggleDB = new HaggleDB(getApplicationContext());


    }

    /** submit button clicked. need to upload shiza to the db */
    public void DBUpload(View view) {

        EditText title = (EditText) findViewById(R.id.title_text);
        EditText address = (EditText) findViewById(R.id.address_text);
        EditText description = (EditText) findViewById(R.id.description_text);
        EditText price = (EditText) findViewById(R.id.price_text);
        ImageView imgView = (ImageView) findViewById(R.id.imageView1);
        Button imgbut = (Button) findViewById(R.id.imagebtn);

        // bitmap stuff to put image in db
        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver() , realPhoto);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            img = bos.toByteArray();
        }
        catch (Exception e)
        {
            // handle exception
            Log.e(TAG, "error handling bitmap");
        }

        String titleText = title.getText().toString().toLowerCase();
        String addressText = address.getText().toString();
        String descriptionText = description.getText().toString();
        double priceVal = Double.valueOf(price.getText().toString());

        db = mHaggleDB.getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put(KEY_TITLE, titleText);
        vals.put(KEY_PRICE, priceVal);
        vals.put(KEY_ADDR, addressText);
        vals.put(KEY_DESC, descriptionText);
        vals.put(KEY_IMG, img);
        db.insert("item", null, vals);

        // message sent to let user know updated
        Toast.makeText(this, "Upload Successful", Toast.LENGTH_SHORT).show();

        // clear all text and remove image
        title.setText("");
        address.setText("");
        price.setText("");
        description.setText("");
        imgView.setVisibility(View.INVISIBLE);
        imgbut.setVisibility(View.VISIBLE);

        Intent i = new Intent(UploadActivity.this, HandheldActivity.class);
        startActivity(i);


    }


    /** click to upload an image. need a camera intent and keep photo in this spot */
    public void cameraOpen(View view) {
        dispatchTakePictureIntent();
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
        image.setImageURI(realPhoto);
        image.getLayoutParams().width = 1000;
        image.getLayoutParams().height = 500;
        btn.setVisibility(View.INVISIBLE);
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
