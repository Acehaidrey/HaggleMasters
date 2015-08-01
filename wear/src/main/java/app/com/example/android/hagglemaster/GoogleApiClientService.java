package app.com.example.android.hagglemaster;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.wearable.Node;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class GoogleApiClientService extends IntentService {

    private Set<Node> nodes = null;
    private Node node = null;
    private GoogleApiClient myGoogleApiClient;
    private static final String RECEIVER_SERVICE_PATH = "/receiver-service";
    private static final String TAG = "GoogleApiClientTAG";


    public GoogleApiClientService() {
        super("GoogleApiClientService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            initGoogleAPI();
            sendMessage();
            myGoogleApiClient.disconnect();
        }
    }

    public void initGoogleAPI() {
        myGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "onConnected: " + bundle);
                        // Do something
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "onConnectionSuspended: " + i);
                        // blank
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed: " + connectionResult);
                        // blank
                    }
                })
                .addApi(Wearable.API)
                .build();

        myGoogleApiClient.connect();

        CapabilityApi.GetCapabilityResult capResult =
                Wearable.CapabilityApi.getCapability(myGoogleApiClient, "wearCapability",
                        CapabilityApi.FILTER_REACHABLE)
                        .await();
        nodes = capResult.getCapability().getNodes();

        if (nodes.size() > 0) {
            node = nodes.iterator().next();
        }
    }

    private void sendMessage() {
        if (node != null) {
            Log.d(TAG, "Message sent");
            Wearable.MessageApi.sendMessage(myGoogleApiClient,
                    node.getId(), RECEIVER_SERVICE_PATH, null)
                    .await();
        }
    }

}
