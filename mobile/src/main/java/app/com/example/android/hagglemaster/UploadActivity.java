package app.com.example.android.hagglemaster;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;


public class UploadActivity extends ActionBarActivity {
    private HaggleDB mHaggleDB;
    private SQLiteDatabase db;

    private static final String KEY_TITLE = "title";
    private static final String KEY_ADDR = "address";
    private static final String KEY_DESC = "description";
    private static final String KEY_IMG = "image";
    private static final String[] COLUMNS = {KEY_TITLE, KEY_ADDR, KEY_DESC, KEY_IMG};
    private static final String TAG = "UploadActivityTAG";

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

        String titleText = title.getText().toString();
        String addressText = address.getText().toString();
        String descriptionText = description.getText().toString();

        Log.d(TAG, titleText + " " + addressText + " " + descriptionText);

        db = mHaggleDB.getWritableDatabase();
        ContentValues vals = new ContentValues();
//        vals.put(KEY_IMG, null); // THIS IS JUST TEMPORARY FIX
        vals.put(KEY_TITLE, titleText);
        vals.put(KEY_ADDR, addressText);
        vals.put(KEY_DESC, descriptionText);
        long newRowId = db.insert("item", null, vals);



        // this is just to check shit got uploaded to DB
        db = mHaggleDB.getReadableDatabase();
        String[] columns = { "address" };
        Cursor c = db.query("item", columns, null, null, null, null, null);
        c.moveToFirst();
        String addr = c.getString(c.getColumnIndex("address"));
        Log.v("ADDR_TAG", addr);


    }

    /** click to upload an image. need a camera intent and keep photo in this spot */
    public void cameraOpen(View view) {
        // fill in
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
