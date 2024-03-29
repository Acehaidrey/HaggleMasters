package app.com.example.android.hagglemaster;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.support.v4.content.LocalBroadcastManager;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;



public class HandheldActivity extends Activity implements Animation.AnimationListener {

    private boolean visible = true;
    public static final String DATABASE_NAME = "Haggle.db";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESC = "description";
    private static final String KEY_IMG = "image";
    private static final String KEY_PRICE = "price";

    private static final String KEY_DATE = "date";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";
    private static final String KEY_RATING = "rating";

    private static final String TAG = HandheldActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    TextView title;
    Animation animFadein;

    private HaggleDB mHaggleDB;
    private SQLiteDatabase db;
    private ArrayList<String> queryTitle;
    private ArrayList<Double> queryPrice;
    private ArrayList<String> queryDescription;
    private ArrayList<byte[]> queryImage;

    private ArrayList<String> queryDate;
    private ArrayList<Float> queryRating;
    private ArrayList<Double> queryLatitude;
    private ArrayList<Double> queryLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handheld);

//        getApplicationContext().deleteDatabase(DATABASE_NAME);
        mHaggleDB = new HaggleDB(getApplicationContext());

        TextView t = (TextView) findViewById(R.id.title);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Pacifico.ttf");
        t.setTypeface(type);

        EditText t1 = (EditText) findViewById(R.id.search_query);
        Typeface type1 = Typeface.createFromAsset(getAssets(),"fonts/Raleway-Italic.ttf");
        t1.setTypeface(type1);

        title = (TextView) findViewById(R.id.title);
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        animFadein.setDuration(3500);
        title.startAnimation(animFadein);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("upload!!!"));


        final EditText searchQuery = (EditText) findViewById(R.id.search_query);
        final Button shareButton = (Button) findViewById(R.id.hagglehelper);
        searchQuery.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int i, KeyEvent event) {
                String text = searchQuery.getText().toString();
                if (text.matches("")) {
                    if (!visible) {
                        shareButton.setVisibility(View.VISIBLE);
                        visible = true;
                    }
                } else {
                    shareButton.setVisibility(View.INVISIBLE);
                    visible = false;
                }
            return false;
            }
        });
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String action = intent.getAction();
            Log.d(TAG, "upload!!!!");
            Intent uploadIntent = new Intent(getApplicationContext(), UploadActivity.class);
            startActivity(uploadIntent);
        }
    };

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
    public void startSearch(View view) {

        queryTitle = new ArrayList<String>();
        queryPrice = new ArrayList<Double>();
        queryDescription = new ArrayList<String>();
        queryImage = new ArrayList<byte[]>();
        queryDate = new ArrayList<String>();
        queryRating = new ArrayList<Float>();
        queryLatitude = new ArrayList<Double>();
        queryLongitude = new ArrayList<Double>();

        EditText searchText = (EditText) findViewById(R.id.search_query);
        String query = searchText.getText().toString().toLowerCase().trim();

        db = mHaggleDB.getWritableDatabase();
        String[] columns = {KEY_TITLE, KEY_DESC, KEY_PRICE, KEY_IMG, KEY_DATE, KEY_RATING, KEY_LAT, KEY_LONG};
        String predicate = "title = ?";
        String[] predicate_values = {query};
        String orderBy = "price ASC";

        Cursor c;
        c = db.query("item", columns, predicate, predicate_values, null, null, orderBy);

        if (c.getCount() > 0) {
            c.moveToFirst();
//            Log.d(TAG, " does it recognize " + c.getString(c.getColumnIndex(KEY_TITLE)));
            String titlel, desc, dateStr, cap;
            double prc, lat, lon;
            byte[] pic;
            float rating;

            do {
                titlel = c.getString(c.getColumnIndex(KEY_TITLE));
                cap = titlel.substring(0, 1).toUpperCase() + titlel.substring(1);
                desc = c.getString(c.getColumnIndex(KEY_DESC));
                prc = c.getDouble(c.getColumnIndex(KEY_PRICE));
                pic = c.getBlob(c.getColumnIndex(KEY_IMG));

                dateStr = c.getString(c.getColumnIndex(KEY_DATE));
                rating = c.getFloat(c.getColumnIndex(KEY_RATING));
                lat = c.getDouble(c.getColumnIndex(KEY_LAT));
                lon = c.getDouble(c.getColumnIndex(KEY_LONG));

                queryTitle.add(cap);
                queryDescription.add(desc);
                queryPrice.add(prc);
                queryImage.add(pic);

                queryRating.add(rating);
                queryLatitude.add(lat);
                queryLongitude.add(lon);
                queryDate.add(dateStr);

            } while (c.moveToNext());

            Intent resultsIntent = new Intent(this, ResultsActivity.class);
            resultsIntent.putExtra("queryItem", query);
            resultsIntent.putStringArrayListExtra("titleAL", queryTitle);
            resultsIntent.putStringArrayListExtra("descriptionAL", queryDescription);
            resultsIntent.putExtra("priceAL", queryPrice);
            resultsIntent.putExtra("imageAL", queryImage);
            resultsIntent.putStringArrayListExtra("dateAL", queryDate);
            resultsIntent.putExtra("ratingAL", queryRating);
            resultsIntent.putExtra("latAL", queryLatitude);
            resultsIntent.putExtra("longAL", queryLongitude);

            startActivity(resultsIntent);

        } else {
            Toast toast = Toast.makeText(this, "Sorry, item not found :(\nPlease search for another item!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, -100);
            toast.show();
        }
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

    /** upload now clicked */
    public void uploadNow(View view) {
        Intent uploadIntent = new Intent(this, UploadActivity.class);
        startActivity(uploadIntent);
    }

}


