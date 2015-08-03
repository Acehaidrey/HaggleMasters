package app.com.example.android.hagglemaster;

import android.app.Activity;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchDetails extends Activity {
   // private static final String TAG = "detailsTAG";

    private String title, desc, loc;
    private double avgprice, finprice;
    private byte[] img;

    private static final String TAG = "SearchDetailActivity";
    private GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getIntentVals();
        display();

        // sets Activity title to the name of the item
        setTitle("[name/title in database]");

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


    }

    private void getIntentVals() {
        Intent detailsIntent = getIntent();
        title = detailsIntent.getStringExtra("title");
        loc = detailsIntent.getStringExtra("address");
        desc = detailsIntent.getStringExtra("description");
        img = detailsIntent.getByteArrayExtra("image");
        avgprice = (double) detailsIntent.getSerializableExtra("avgprice");
        finprice = (double) detailsIntent.getSerializableExtra("price");
        Log.v(TAG, title + " " + loc + " " + desc + " " + avgprice + " " + finprice);

    }

    private void display() {
        TextView des = (TextView) findViewById(R.id.description_text);
        des.setText(desc);

        TextView fin = (TextView) findViewById(R.id.finalprice);

        fin.append("$" + String.valueOf(finprice));

        TextView prc = (TextView) findViewById(R.id.averageprice);
        prc.append("$" + String.valueOf(avgprice));

        ImageView iv = (ImageView) findViewById(R.id.addImg);
        Bitmap bm = BitmapFactory.decodeByteArray(img, 0, img.length);
        iv.setImageBitmap(bm);
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
