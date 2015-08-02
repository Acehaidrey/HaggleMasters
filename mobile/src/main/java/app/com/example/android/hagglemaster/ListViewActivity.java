package app.com.example.android.hagglemaster;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataApi.DataItemResult;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.PutDataRequest;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by whitneyxu on 7/31/15.
 */
public class ListViewActivity extends ActionBarActivity {


    ListView listView;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "PhoneActivity";
//    String[] values = new String[]{"Send Price   ",
//            "Bargain Lower",
//            "To Leave     ",
//            "Take it      "
//    };
//    String[] valuesTemp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylist);
        // Get ListView object from xml


        mGoogleApiClient = new GoogleApiClient.Builder(ListViewActivity.this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();



        Button buttonHigher = (Button) findViewById(R.id.higher);
        buttonHigher.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendNotification("Higher Price", "");
                Log.d("does it call", "highr");
            }
        });

        Button buttonLower = (Button) findViewById(R.id.lower);
        buttonLower.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendNotification("Lower Price","");
            }
        });

        Button buttonTake = (Button) findViewById(R.id.take);
        buttonTake.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendNotification("Take it","");
            }
        });

        Button buttonLeave = (Button) findViewById(R.id.leave);
        buttonLeave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendNotification("Leave it ","");
            }
        });
        Button mButton = (Button)findViewById(R.id.send);
        mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        EditText edt = (EditText)findViewById(R.id.editPrice);
                        EditText edt2 = (EditText)findViewById(R.id.editMessage);
                        String price = edt.getText().toString();
                        String message = edt2.getText().toString();
                        Log.d("Price", price);
                        Log.d("Message", message);
                        String msgTog = price + message;
                        sendNotification(price, message);
                        Log.d("Tog", msgTog);
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }


    private String now() {
        DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(this);
        return dateFormat.format(new Date());
    }

    private void sendNotification(String s1, String s2) {
        if (mGoogleApiClient.isConnected()) {
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/notification");
            // Make sure the data item is unique. Usually, this will not be required, as the payload
            // (in this case the title and the content of the notification) will be different for almost all
            // situations. However, in this example, the text and the content are always the same, so we need
            // to disambiguate the data item by adding a field that contains teh current time in milliseconds.
            dataMapRequest.getDataMap().putString("TITLE", s1);
            dataMapRequest.getDataMap().putLong("time", new Date().getTime());
            dataMapRequest.getDataMap().putString("TEXT", s2);
            PutDataRequest putDataRequest = dataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
            Log.d("HAHHAHHHA", "WORK HERE");
        } else {
            Log.e(TAG, "No connection to wearable available!");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_handheld, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    }

