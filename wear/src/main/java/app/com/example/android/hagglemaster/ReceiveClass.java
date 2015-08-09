package app.com.example.android.hagglemaster;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.net.Uri;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.wearable.PutDataRequest.WEAR_URI_SCHEME;



/**
 * Created by whitneyxu on 7/8/15.
 */
public class ReceiveClass extends WearableListenerService{

    private static final String RECEIVER_SERVICE_PATH = "/receiver-service";
    private static final String TAG = "ReceiveClass";
    //private static final String START_ACTIVITY_PATH = "/start-activity";
    private int notificationId = 001;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (null != intent) {
//            String action = intent.getAction();
//            if ("ACTION_DISMISS".equals(action)) {
//                dismissNotification();
//            }
//        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.cancel(notificationId);
        Log.d("onDataChange", "here");
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: " + dataEvents);
        }
        final List events = FreezableUtils
                .freezeIterable(dataEvents);

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult =
                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }
        for(DataEvent dataEvent: dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                if ("/notification".equals(dataEvent.getDataItem().getUri().getPath())) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                    String title = dataMapItem.getDataMap().getString("TITLE");
                    String content = dataMapItem.getDataMap().getString("TEXT");
                    Log.d("gotttttt", "stringgggg");
                    //WearActivity.getApplicationContext().finish();
                    sendNotification(title, content);
                }
            }
        }
    }

    private void sendNotification(String title, String content) {

        // this intent will open the activity when the user taps the "open" action on the notification
        Intent viewIntent = new Intent(this, WearActivity.class);
        PendingIntent pendingViewIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        Intent clickIntent = new Intent(this, GoogleApiClientService.class);
        PendingIntent clickPendingIntent =
                PendingIntent.getService(this, 0, clickIntent, 0);
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.cancel(notificationId);
        // this intent will be sent when the user swipes the notification to dismiss it
//        Intent dismissIntent = new Intent(Constants.ACTION_DISMISS);
//        PendingIntent pendingDeleteIntent = PendingIntent.getService(this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Intent purchaseIntent = new Intent(this, UploadActivity.class);
        CharSequence chars = "Take it";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingViewIntent)
                .addAction(R.mipmap.pic1,
                        getString(R.string.purchase), clickPendingIntent);
        if (title.equals("Lower Price")){
            builder.setVibrate(new long[]{0, 500, 500, 500});
            Bitmap bm = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.arrowdown);
            builder.extend(new NotificationCompat.WearableExtender()
                    .setBackground(bm));
        } else if (title.equals("Leave it")){
            builder.setVibrate(new long[]{0, 200});
            Bitmap bm = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.redcheck);
            builder.extend(new NotificationCompat.WearableExtender().setBackground(bm));
        } else if (title.equals("Take it")){
            builder.setVibrate(new long[]{0, 2000});
            Bitmap bm = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.greencheck);
            builder.extend(new NotificationCompat.WearableExtender().setBackground(bm));
        } else if (title.equals("Higher Price")){
            builder.setVibrate(new long[]{0, 500, 500, 500, 500, 500});
            Bitmap bm = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.arrowup);
            builder.extend(new NotificationCompat.WearableExtender().setBackground(bm));
        } else {
            builder.setVibrate(new long[]{0, 200, 200, 200, 200, 200});
            Bitmap bm = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.mail);
            builder.extend(new NotificationCompat.WearableExtender().setBackground(bm));
        }

        Notification notification = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId++, notification);

    }

//    private void dismissNotification() {
//        new DismissNotificationCommand(this).execute();
//    }


    private class DismissNotificationCommand implements GoogleApiClient.ConnectionCallbacks, ResultCallback<DataApi.DeleteDataItemsResult>, GoogleApiClient.OnConnectionFailedListener {

        private static final String TAG = "DismissNotification";

        private final GoogleApiClient mGoogleApiClient;

        public DismissNotificationCommand(Context context) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        public void execute() {
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnected(Bundle bundle) {
            final Uri dataItemUri =
                    new Uri.Builder().scheme(WEAR_URI_SCHEME).path("/notification").build();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Deleting Uri: " + dataItemUri.toString());
            }
            Wearable.DataApi.deleteDataItems(
                    mGoogleApiClient, dataItemUri).setResultCallback(this);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "onConnectionSuspended");
        }

        @Override
        public void onResult(DataApi.DeleteDataItemsResult deleteDataItemsResult) {
            if (!deleteDataItemsResult.getStatus().isSuccess()) {
                Log.e(TAG, "dismissWearableNotification(): failed to delete DataItem");
            }
            mGoogleApiClient.disconnect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed");
        }


    }

}






