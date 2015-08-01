package app.com.example.android.hagglemaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;
import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class WearActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);

        // start messaging through GoogleApiClient
        Intent serviceIntent = new Intent(getApplicationContext(), GoogleApiClientService.class);
        startService(serviceIntent);

        /*
        mTextView = (TextView)findViewById(R.id.text);

        Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        mTextView.startAnimation(animationFadeIn);
        */
    }
    /*
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mTextView.clearAnimation();
    }
    */
}
