package app.com.example.android.hagglemaster;

import android.app.Activity;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SearchDetails extends FragmentActivity {

    private String title, desc, date;
    private double avgprice, finprice, latit, longit;
    private byte[] img;
    private float rating;
    private GoogleMap mMap;

    private static final String TAG = SearchDetails.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_details);
        getIntentVals();
        display();
        timeStamp();
        setUpMapIfNeeded();

        Button b = (Button) findViewById(R.id.hagglehelper);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent intentList = new Intent(SearchDetails.this, ListViewActivity.class);
                intentList.putExtra("avgprice", avgprice);
                intentList.putExtra("lastprice", finprice);
                startActivity(intentList);
                Intent wear = new Intent(SearchDetails.this, SendingService.class);
                SearchDetails.this.startService(wear);
            }
        });

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(longit, latit)).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(longit, latit)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /** get all the intent values */
    private void getIntentVals() {
        Intent detailsIntent = getIntent();
        title = detailsIntent.getStringExtra("title");
        desc = detailsIntent.getStringExtra("description");
        img = detailsIntent.getByteArrayExtra("image");
        date = detailsIntent.getStringExtra("date");
        latit = (double) detailsIntent.getSerializableExtra("latitude");
        longit = (double) detailsIntent.getSerializableExtra("longitude");
        rating = (float) detailsIntent.getSerializableExtra("rating");

        avgprice = (double) detailsIntent.getSerializableExtra("avgprice");
        finprice = (double) detailsIntent.getSerializableExtra("price");

    }

    /** show all the values on the UI screen */
    private void display() {
        TextView nameTV = (TextView) findViewById(R.id.name);
        String cap = title.substring(0, 1).toUpperCase() + title.substring(1);
        nameTV.setText(cap);

        TextView des = (TextView) findViewById(R.id.description_text);
        des.setText(desc);

        TextView fin = (TextView) findViewById(R.id.finalprice);

        fin.append(" $" + String.valueOf(new DecimalFormat("#.00").format(finprice)));

        TextView prc = (TextView) findViewById(R.id.averageprice);
        prc.append(" $" + String.valueOf(new DecimalFormat("#.00").format(avgprice)));

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        Bitmap bm = BitmapFactory.decodeByteArray(img, 0, img.length);
        iv.setImageBitmap(bm);

        RatingBar rb = (RatingBar) findViewById(R.id.ratingBar); //TODO: implement live
        rb.setRating(rating);


    }

    /** get the timestamp. */
    private void timeStamp() {
        DateFormat df = DateFormat.getDateInstance();
        TextView timeStamp = (TextView) findViewById(R.id.timestamp);
        try {
            Date date2 = new Date();
            Date date1 = df.parse(date); // TODO: implement this
            Log.v(TAG, "date today: " + df.format(date1) + " date2 set: " + df.format(date2));
            long diff = getDateDiff(date1, date2, TimeUnit.DAYS);
            Log.v(TAG, "difference: " + String.valueOf(diff));

            if (diff < 1) {
                timeStamp.setText("last purchase made today");
            } else {
                timeStamp.setText("last purchase made " + String.valueOf(diff) + " days ago");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /** get difference between dates Make sure date2 is larger than date1 */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_details, menu);
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
