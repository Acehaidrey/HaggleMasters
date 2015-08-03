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
public class MyReceiverService extends WearableListenerService{

    private static final String RECEIVER_SERVICE_PATH = "/receiver-service";
    private static final String TAG = "MyReceiverService";
    //private static final String START_ACTIVITY_PATH = "/start-activity";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //super.onMessageReceived(messageEvent);
        Log.d("HERE", "WORK");
        Intent uploadIntent = new Intent("upload!!!");
        broadCastHelper(uploadIntent);
    }


    private void broadCastHelper(Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}



