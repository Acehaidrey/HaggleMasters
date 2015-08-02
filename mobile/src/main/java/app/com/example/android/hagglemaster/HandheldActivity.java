package app.com.example.android.hagglemaster;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class HandheldActivity extends Activity implements Animation.AnimationListener {

    private static final String DATABASE_NAME = "item";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ADDR = "address";
    private static final String KEY_DESC = "description";
    private static final String KEY_IMG = "image";
    private static final String KEY_PRICE = "price";
    private static final String[] COLUMNS = {KEY_TITLE, KEY_PRICE, KEY_ADDR, KEY_DESC, KEY_IMG};

    private static final String TAG = "handheldMainTAG";

    TextView title;
    Animation animFadein;

    private HaggleDB mHaggleDB;
    private SQLiteDatabase db;
    private ArrayList<String> queryTitle;
    private ArrayList<Double> queryPrice;
    private ArrayList<String> queryAddress;
    private ArrayList<String> queryDescription;
//    private ArrayList<String> queryImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handheld);
        mHaggleDB = new HaggleDB(getApplicationContext());
        queryTitle = new ArrayList<String>();
        queryPrice = new ArrayList<Double>();
        queryAddress = new ArrayList<String>();
        queryDescription = new ArrayList<String>();


//        Intent listView = new Intent(this, ListViewActivity.class);
//        startActivity(listView);


//        Intent uploadIntent = new Intent(getApplicationContext(), UploadActivity.class);
//        startActivity(uploadIntent);


        title = (TextView) findViewById(R.id.title);
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        animFadein.setDuration(3500);
        title.startAnimation(animFadein);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_handheld, menu);
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
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // Take any action after completing the animation

        // check for fade in animation
        if (animation == animFadein) {
            Toast.makeText(getApplicationContext(), "Animation Stopped",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /** on click for search icon */
    // TODO: if item doesn't exist in our DB or if pass in nothing don't break!
    public void startSearch(View view) {

        EditText searchText = (EditText) findViewById(R.id.search_query);
        String query = searchText.getText().toString();
//        .toLowerCase();
        Log.d(TAG, "Query is " + query);


        // this is just to check shit got uploaded to DB
        db = mHaggleDB.getReadableDatabase();
        String[] columns = {KEY_TITLE, KEY_ADDR, KEY_DESC, KEY_PRICE};
        String predicate = "title = ?";
        String[] predicate_values = {query};
        String orderBy = "price ASC";

        Cursor c = db.query("item", columns, predicate, predicate_values, null, null, orderBy);

        if (c != null) {
            c.moveToFirst();
            String titlel = c.getString(c.getColumnIndex(KEY_TITLE));
            String addr = c.getString(c.getColumnIndex(KEY_ADDR));
            String desc = c.getString(c.getColumnIndex(KEY_DESC));
            double prc = c.getDouble(c.getColumnIndex(KEY_PRICE));

            queryTitle.add(titlel);
            queryAddress.add(addr);
            queryDescription.add(desc);
            queryPrice.add(prc);

            while (c.moveToNext()) {
                titlel = c.getString(c.getColumnIndex(KEY_TITLE));
                addr = c.getString(c.getColumnIndex(KEY_ADDR));
                desc = c.getString(c.getColumnIndex(KEY_DESC));
                prc = c.getDouble(c.getColumnIndex(KEY_PRICE));

                queryTitle.add(titlel);
                queryAddress.add(addr);
                queryDescription.add(desc);
                queryPrice.add(prc);
            }

            Intent resultsIntent = new Intent(this, ResultsActivity.class);
            resultsIntent.putExtra("queryItem", query);
            resultsIntent.putStringArrayListExtra("addressAL", queryAddress);
            resultsIntent.putStringArrayListExtra("titleAL", queryTitle);
            resultsIntent.putExtra("priceAL", queryPrice);
            startActivity(resultsIntent);


        } else {
            // TODO: make exception for wrong input or blank input
            Toast.makeText(this, "Sorry, item not found ):", Toast.LENGTH_SHORT).show();
        }
    }
}
