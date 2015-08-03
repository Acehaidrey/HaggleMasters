package app.com.example.android.hagglemaster;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;


public class ResultsActivity extends Activity {
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

        dynamicDisplay();

    }

    /** display things to the xml in a scrollable fashion */
    private void dynamicDisplay() {
        ScrollView sv = (ScrollView) findViewById(R.id.scrollView1);
        LinearLayout ll = new LinearLayout(this);
        LinearLayout.LayoutParams pars = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        ll.setLayoutParams(pars);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);


        for (int i = 0; i < titleResults.size(); i++) {

            LinearLayout newll = new LinearLayout(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            p.setMargins(0, 0, 0, dptopx(2));
            newll.setLayoutParams(p);
            newll.setOrientation(LinearLayout.HORIZONTAL);
            newll.setBackgroundColor(getResources().getColor(R.color.blu));

            LinearLayout linleft = new LinearLayout(this);
            linleft.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2.0f));
            LinearLayout linright = new LinearLayout(this);

            linright.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3.0f));

            ImageView iv = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(params);
            iv.setMaxHeight(dptopx(40));
            iv.setMinimumHeight(dptopx(40));
            iv.setMaxWidth(dptopx(800));
            iv.setScaleType(ImageView.ScaleType.FIT_XY);

            Bitmap bm = BitmapFactory.decodeByteArray(imageResults.get(i), 0, imageResults.get(i).length);
            iv.setImageBitmap(bm);

            String show = "<strong>Item Name: </strong>" + titleResults.get(i) + "<br>" +
                    "<strong>Address: </strong>" + addressResults.get(i) + "<br>" +
                    "<strong>Avg. Price: </strong>$" + avgPrice(priceResults) + "<br>" + "<strong>Last Price: </strong>$" +
                    new DecimalFormat("#.00").format(priceResults.get(i));

            linleft.addView(iv);
            TextView tv = new TextView(this);
            tv.setPadding(dptopx(35), dptopx(45), 0, 0); // hard code. fix for final to center
//            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setLineSpacing(2.5f, 1);

            tv.setText(Html.fromHtml(show));
            tv.setTextColor(getResources().getColor(R.color.off_white));
            linright.addView(tv);

            newll.addView(linleft);
            newll.addView(linright);
            newll.setClickable(true);
            newll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent upload = new Intent(ResultsActivity.this, UploadActivity.class);
                    startActivity(upload);

                }
            });


            ll.addView(newll);

        }

    }


    /** converts dp to pixels */
    private int dptopx(int px) {
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (px * scale + 0.5f);
        return padding_in_px;
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
        double min = al.get(minIndex);
        return (double) Math.round(min * 100) / 100;
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
