package app.com.example.android.hagglemaster;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by whitneyxu on 8/3/15.
 */
public class StartMianActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_wear);
        Log.d("hhaha", "it worked");
        TextView mTextView = (TextView) findViewById(R.id.logotext);
        Typeface typeFace = Typeface.createFromAsset(getAssets(),"fonts/Pacifico.ttf");
        mTextView.setTypeface(typeFace);
//        finish();

    }
}
