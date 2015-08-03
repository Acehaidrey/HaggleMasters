package app.com.example.android.hagglemaster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;


public class ResultsActivity extends ActionBarActivity {
    private static final String TAG = "resultsTAG";
    private String querySearch;
    private ArrayList<String> addressResults, titleResults;
    private ArrayList<Double> priceResults;
    private ArrayList<byte[]> imageResults;

    double minVal, avgVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        recoverIntentData();

        // setting the text to Search results for... query item
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText("Search Results for: " + querySearch);

        // getting average and min prices to display to users
        avgVal = avgPrice(priceResults);
        minVal = minPrice(priceResults);

        String s = "";
        TextView tv = (TextView) findViewById(R.id.textview1);
        ImageView iv = (ImageView) findViewById(R.id.imageview1);

        for (byte[] temp : imageResults) {
            s += temp.toString() + ", ";
            Log.v(TAG, temp.toString());
        }
        tv.setText(s);

        Bitmap bm = BitmapFactory.decodeByteArray(imageResults.get(0), 0, imageResults.get(0).length);
        iv.setImageBitmap(bm);

    }

    /** Get intent data */
    private void recoverIntentData() {
        Intent resultsIntent = getIntent();
        querySearch = resultsIntent.getStringExtra("queryItem");
        addressResults = resultsIntent.getStringArrayListExtra("addressAL");
        titleResults = resultsIntent.getStringArrayListExtra("titleAL");
        priceResults = (ArrayList<Double>) resultsIntent.getSerializableExtra("priceAL");
        imageResults = (ArrayList<byte[]>) resultsIntent.getSerializableExtra("imageAL");
    }

    /** Retrieves the minimum price of the list */
    private double minPrice(ArrayList<Double> al) {
        int minIndex = al.indexOf(Collections.min(al));
        return al.get(minIndex);
    }

    /** Retrieves the average price of the list */
    private double avgPrice(ArrayList<Double> al) {
        double sum = 0.0;
        double size = al.size();
        if (size == 0) { return sum; }
        for (double val: al) {
            sum += val;
        }
        double avg = (sum/size);
        return (double) Math.round(avg * 100) / 100;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
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
