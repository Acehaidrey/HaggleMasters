package app.com.example.android.hagglemaster;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;


public class ResultsActivity extends ActionBarActivity {
    private static final String TAG = "resultsTAG";

    double minVal, avgVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent resultsIntent = getIntent();
        String querySearch = resultsIntent.getStringExtra("queryItem");
        ArrayList<String> addressResults = resultsIntent.getStringArrayListExtra("addressAL");
        ArrayList<String> titleResults = resultsIntent.getStringArrayListExtra("titleAL");
        ArrayList<Double> priceResults = (ArrayList<Double>) resultsIntent.getSerializableExtra("priceAL");

        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText("Search Results for: " + querySearch);

        avgVal = avgPrice(priceResults);
        minVal = findMin(priceResults);
        Log.v(TAG, "avg: " + avgVal + " min: " + minVal);

        String s = "";
        TextView tv = (TextView) findViewById(R.id.textview1);
        for (Double temp : priceResults) {
            s += temp + ", ";
        }
        tv.setText(s);
    }

    public double findMin(ArrayList<Double> al) {
        int minIndex = al.indexOf(Collections.min(al));
        return al.get(minIndex);
    }

    public double avgPrice(ArrayList<Double> al) {
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
