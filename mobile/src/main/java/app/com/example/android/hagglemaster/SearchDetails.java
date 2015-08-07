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

import android.widget.EditText;
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

    private String title, desc, loc, date;
    private double avgprice = 0.0 , finprice = 0.0 , latit, longit;
    private byte[] img;
    private float rating;
    private GoogleMap googleMap;

    private static final String TAG = SearchDetails.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_details);
        getIntentVals();
        display();

        TextView t = (TextView) findViewById(R.id.name);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Raleway-Italic.ttf");
        t.setTypeface(type);
        TextView t1 = (TextView) findViewById(R.id.finalprice);
        Typeface type1 = Typeface.createFromAsset(getAssets(),"fonts/Raleway-Italic.ttf");
        t1.setTypeface(type1);
        TextView t2 = (TextView) findViewById(R.id.averageprice);
        Typeface type2 = Typeface.createFromAsset(getAssets(),"fonts/Raleway-Italic.ttf");
        t2.setTypeface(type2);



        timeStamp();


        Button b = (Button) findViewById(R.id.hagglehelper);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent intentList = new Intent(SearchDetails.this, ListViewActivity.class);
                startActivity(intentList);
                Intent wear = new Intent(SearchDetails.this, SendingService.class);
                SearchDetails.this.startService(wear);
            }
        });



        // =============================== Google Map ==============================================
//        locationStr = extras.getString("ENCOUNTER_LOCATION");
//        String[] locationArray = extras.getString("ENCOUNTER_LOCATION").split(",");
//        double encounterLatitude = Double.parseDouble(locationArray[0]);
//        double encounterLongitude = Double.parseDouble(locationArray[1]);

//        LatLng itemLocation = new LatLng(longit,latit);
        LatLng itemLocation = new LatLng(0,0);

        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting GoogleMap object from the fragment
        googleMap = fm.getMap();

        // Enabling MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            // Getting latitude of the current location
            double latitude = location.getLatitude();

            // Getting longitude of the current location
            double longitude = location.getLongitude();

            // Creating a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            //myPosition = new LatLng(latitude, longitude);

            //MarkerOptions currentMarker = new MarkerOptions().position(myPosition).title("You");
            MarkerOptions animalMarker = new MarkerOptions().position(itemLocation).title("Name");

            ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();
            //markers.add(currentMarker);
            markers.add(animalMarker);

            googleMap.addMarker(animalMarker);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (MarkerOptions marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();

            int padding = 100; // offset from edges of the map in pixels
            final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);


            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    googleMap.moveCamera(cu); } });
        } else {
            Log.v("Detailed View!!!", "cant find location detailed");
        }
        // =============================== Google Map ==============================================

    }

    private void getIntentVals() {
        Intent detailsIntent = getIntent();
        title = detailsIntent.getStringExtra("title");
        loc = detailsIntent.getStringExtra("address");
        desc = detailsIntent.getStringExtra("description");
        img = detailsIntent.getByteArrayExtra("image");
//        date = detailsIntent.getStringExtra("date"); //TODO: implement live
//        latit = (double) detailsIntent.getSerializableExtra("latitude");
//        longit = (double) detailsIntent.getSerializableExtra("longitude");
//        rating = (float) detailsIntent.getSerializableExtra("rating");

        avgprice = (double) detailsIntent.getSerializableExtra("avgprice");
        finprice = (double) detailsIntent.getSerializableExtra("price");
        Log.v(TAG, title + " " + loc + " " + desc + " " + avgprice + " " + finprice);

    }

    private void display() {
        TextView nameTV = (TextView) findViewById(R.id.name);
        String cap = title.substring(0, 1).toUpperCase() + title.substring(1);
        nameTV.setText(cap);

        TextView des = (TextView) findViewById(R.id.description_text);
        des.setText(desc);

        TextView fin = (TextView) findViewById(R.id.finalprice);

        fin.append("$" + String.valueOf(new DecimalFormat("#.00").format(finprice)));

        TextView prc = (TextView) findViewById(R.id.averageprice);
        prc.append("$" + String.valueOf(new DecimalFormat("#.00").format(avgprice)));

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        Bitmap bm = BitmapFactory.decodeByteArray(img, 0, img.length);
        iv.setImageBitmap(bm);

//        RatingBar rb = (RatingBar) findViewById(R.id.ratingBar); //TODO: implement live
//        rb.setRating(rating);


    }

    /** get the timestamp. */
    private void timeStamp() {
        DateFormat df = DateFormat.getDateInstance();
        TextView timeStamp = (TextView) findViewById(R.id.timestamp);
        try {
            Date date2 = new Date();
//            Date date1 = df.parse(date); // TODO: implement this
            Date date1 = df.parse("August 3, 2015");
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
