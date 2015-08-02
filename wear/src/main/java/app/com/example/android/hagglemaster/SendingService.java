package app.com.example.android.hagglemaster;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by whitneyxu on 7/8/15.
 */
public class SendingService extends IntentService {

    private static final String TAG = "SendingService";
    private GoogleApiClient mGoogleApiClient;
    public static final String CAPABILITY_NAME = "do_stuff";
    public static final String RECEIVER_SERVICE_PATH = "/receiver-service";//recieve
    private String nodeId = null;

    public SendingService() {
        //super();
        super("SendingService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(001);
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks(){
                    @Override
                    public void onConnected(Bundle bundle){
                        //do something
                    }
                    @Override
                    public void onConnectionSuspended(int i) {
                        //do something
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        // Do something
                    }
                })
                .addApi(Wearable.API).build();
        this.mGoogleApiClient.connect();
        Log.d(TAG, "success2!!");

        //step 3
        CapabilityApi.GetCapabilityResult capResult = Wearable.CapabilityApi.getCapability
                (mGoogleApiClient, CAPABILITY_NAME, CapabilityApi.FILTER_REACHABLE).await();

        //step 4:
        for (Node node:capResult.getCapability().getNodes())
        {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), RECEIVER_SERVICE_PATH, new byte[3]);//"message".getBytes()
            Log.d(TAG, "success3!!");
        }

    }


}


