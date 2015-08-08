package app.com.example.android.hagglemaster;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;


public class ResultsActivity extends Activity {
    private static final String TAG = ResultsActivity.class.getSimpleName();
    private String querySearch;
    private ArrayList<String> titleResults, descriptionResults, dateResults;
    private ArrayList<Double> priceResults, latResults, longResults;
    private ArrayList<byte[]> imageResults;
    private ArrayList<Float> ratingResults;

    private double avgVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        recoverIntentData();
        // setting the text to Search results for... query item
        TextView titleView = (TextView) findViewById(R.id.title);

        Typeface type1 = Typeface.createFromAsset(getAssets(),"fonts/Raleway-Italic.ttf");
        titleView.setTypeface(type1);

        String cap = querySearch.substring(0, 1).toUpperCase() + querySearch.substring(1);
        titleView.setText("Search Results for: " + cap);

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

        avgVal = avgPrice(priceResults);
        for (int i = 0; i < titleResults.size(); i++) {

            final int j = i;
            LinearLayout newll = new LinearLayout(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            p.setMargins(0, 0, 0, dptopx(2));
            newll.setLayoutParams(p);
            newll.setOrientation(LinearLayout.HORIZONTAL);
            newll.setBackgroundColor(getResources().getColor(R.color.offwhite));

            LinearLayout linleft = new LinearLayout(this);
            linleft.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2.0f));
            LinearLayout linright = new LinearLayout(this);
            linright.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 3.0f));
            linright.setOrientation(LinearLayout.VERTICAL);

            ImageView iv = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(params);
            iv.setMaxHeight(dptopx(30));
            iv.setMinimumHeight(dptopx(30));
            iv.setMaxWidth(dptopx(800));
            iv.setScaleType(ImageView.ScaleType.FIT_XY);

            // if no image
            Bitmap noImage = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            noImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            final byte[] img = bos.toByteArray();

            if (imageResults.get(i) != null) {
                Bitmap bm = BitmapFactory.decodeByteArray(imageResults.get(i), 0, imageResults.get(i).length);
                iv.setImageBitmap(bm);
            } else {
                Bitmap noImageBM = BitmapFactory.decodeByteArray(img, 0, img.length);
                iv.setImageBitmap(noImageBM);
//                iv.setImageResource(R.drawable.noimage);
            }


            String show = "<strong>Item Name: </strong>" + titleResults.get(i) + "<br>" +
                    "<strong>Date: </strong>" + dateResults.get(i) + "<br>" +
                    "<strong>Avg. Price: </strong>$" + new DecimalFormat("#.00").format(avgVal) +
                    "<br>" + "<strong>Last Price: </strong>$" + new DecimalFormat("#.00").format(priceResults.get(i));

            linleft.addView(iv);
            LinearLayout linTop = new LinearLayout(this);
            linTop.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linTop.setGravity(Gravity.CENTER);
            linTop.setPadding(0, dptopx(30), 0, 0);
            LinearLayout linBot = new LinearLayout(this);
            linBot.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dptopx(25)));
            linBot.setGravity(Gravity.CENTER);
            linBot.setPadding(0, dptopx(10), 0, 0);
            TextView tv = new TextView(this);
            //added for font
            Typeface type1 = Typeface.createFromAsset(getAssets(),"fonts/Raleway-Italic.ttf");
            tv.setTypeface(type1);
            //end
            tv.setLineSpacing(2.5f, 1);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.textsize)); //TODO: see works
            tv.setText(Html.fromHtml(show));
            tv.setTextColor(getResources().getColor(android.R.color.black));
            linTop.addView(tv);
            //RatingBar rating = new RatingBar(getApplicationContext(), null, android.R.attr.ratingBarStyleSmall);
            RatingBar rating = new RatingBar(new ContextThemeWrapper(getApplicationContext(), R.style.RatingBarNewSmall), null, 0);
            rating.setRating(ratingResults.get(i));
            rating.setNumStars(5);
//            ViewGroup.LayoutParams lp = rating.getLayoutParams();
//            lp.height = 15;
//            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            rating.setIsIndicator(true);
            rating.setMinimumHeight(15);
            linBot.addView(rating);
            linright.addView(linTop);
            linright.addView(linBot);
            newll.addView(linleft);
            newll.addView(linright);
            newll.setClickable(true);
            newll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailsIntent = new Intent(ResultsActivity.this, SearchDetails.class);
                    detailsIntent.putExtra("title", titleResults.get(j));
                    detailsIntent.putExtra("description", descriptionResults.get(j));
                    if (imageResults.get(j) != null) {
                        detailsIntent.putExtra("image", imageResults.get(j));
                    } else {
                        detailsIntent.putExtra("image", img);
                    }
                    detailsIntent.putExtra("price", priceResults.get(j));
                    detailsIntent.putExtra("avgprice", avgVal);

                    detailsIntent.putExtra("rating", ratingResults.get(j));
                    detailsIntent.putExtra("latitude", latResults.get(j));
                    detailsIntent.putExtra("longitude", longResults.get(j));
                    detailsIntent.putExtra("date", dateResults.get(j));
                    startActivity(detailsIntent);
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
        titleResults = resultsIntent.getStringArrayListExtra("titleAL");
        descriptionResults = resultsIntent.getStringArrayListExtra("descriptionAL");
        priceResults = (ArrayList<Double>) resultsIntent.getSerializableExtra("priceAL");
        imageResults = (ArrayList<byte[]>) resultsIntent.getSerializableExtra("imageAL");
        dateResults = resultsIntent.getStringArrayListExtra("dateAL");
        latResults = (ArrayList<Double>) resultsIntent.getSerializableExtra("latAL");
        longResults = (ArrayList<Double>) resultsIntent.getSerializableExtra("longAL");
        ratingResults = (ArrayList<Float>) resultsIntent.getSerializableExtra("ratingAL");

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
