package app.com.example.android.hagglemaster;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.net.Uri;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;



/**
 * Created by whitneyxu on 7/8/15.
 */
public class ReceiveMainPage extends WearableListenerService {

    private static final String RECEIVER_SERVICE_PATH = "/receiver-service-mobile";
    private static final String TAG = "ReceiverMainPage";
    //private static final String START_ACTIVITY_PATH = "/start-activity";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //super.onMessageReceived(messageEvent);
        if (messageEvent.getPath().equals(RECEIVER_SERVICE_PATH)) {
            Log.d(TAG, "WORK");
            Intent main = new Intent(this, StartMianActivity.class);
            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(main);
        }
    }




}



