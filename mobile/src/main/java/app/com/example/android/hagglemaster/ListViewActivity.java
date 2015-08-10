package app.com.example.android.hagglemaster;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by whitneyxu on 7/31/15.
 */
public class ListViewActivity extends Activity {


    ListView listView;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "PhoneActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylist);
        // Get ListView object from xml
        TextView t = (TextView) findViewById(R.id.title);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Pacifico.ttf");
        t.setTypeface(type);
        fontText();
        showPrices();

//        TextView textView = (TextView) findViewById(R.id.date);
//        String Date= DateFormat.getDateTimeInstance().format(new Date());
//        textView.setText(Date);



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
        //new thread
        new Thread(new Runnable() {
            public void run() {





                Button buttonHigher = (Button) findViewById(R.id.higher);
                buttonHigher.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        sendNotification("Higher Price", "");
                        Log.d("does it call", "higher");
                    }
                });

                Button buttonLower = (Button) findViewById(R.id.lower);
                buttonLower.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        sendNotification("Lower Price", "");
                    }
                });

                Button buttonTake = (Button) findViewById(R.id.take);
                buttonTake.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        sendNotification("Take it", "");
                    }
                });

                Button buttonLeave = (Button) findViewById(R.id.leave);
                buttonLeave.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        sendNotification("Leave it", "");
                    }
                });
                ImageButton priceButton = (ImageButton) findViewById(R.id.sendPrice);
                priceButton.setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View view) {
                                EditText edt = (EditText)findViewById(R.id.editPrice);
                                String price = edt.getText().toString();
                                Log.d("Price", price);
                                if (price.matches("")) {
                                    Toast.makeText(getApplicationContext(), "Please enter a number!", Toast.LENGTH_SHORT).show();
                                } else {
                                    sendNotification("Haggle for: $" + new DecimalFormat("#.00").format(Double.parseDouble(price)), "");
                                    edt.setText("");
                                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                            }
                        });
                ImageButton messageButton = (ImageButton) findViewById(R.id.sendMessage);
                messageButton.setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View view) {
                                EditText edt = (EditText)findViewById(R.id.editMessage);
                                String message = edt.getText().toString();
                                Log.d("Message", message);
                                if (message.matches("")) {
                                    Toast.makeText(getApplicationContext(), "Please enter a message!", Toast.LENGTH_SHORT).show();
                                } else {
                                    sendNotification("Message: ", message);
                                    edt.setText("");
                                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                            }
                        });

            }
        }).start();



    }

    /** display prices from details intent */
    private void showPrices() {
        TextView avg = (TextView) findViewById(R.id.averageprice);
        TextView last = (TextView) findViewById(R.id.lastprice);

        Intent priceInfo = getIntent();
        double avgprc = (double) priceInfo.getSerializableExtra("avgprice");
        double lastprc = (double) priceInfo.getSerializableExtra("lastprice");

        avg.append(" $" + new DecimalFormat("#.00").format(avgprc));
        last.append(" $" + new DecimalFormat("#.00").format(lastprc));
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
            Toast.makeText(getApplicationContext(), "Message Sent!", Toast.LENGTH_SHORT).show();
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
    public void fontText(){
        TextView t1 = (TextView)findViewById(R.id.averageprice);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Raleway-Italic.ttf");
        t1.setTypeface(type);
        TextView t2 = (TextView)findViewById(R.id.lastprice);
        t2.setTypeface(type);
        TextView t3 = (TextView)findViewById(R.id.price);
        t3.setTypeface(type);
        TextView t4 = (TextView)findViewById(R.id.message);
        t4.setTypeface(type);
        EditText t5 = (EditText) findViewById(R.id.editPrice);
        t5.setTypeface(type);
        EditText t6 = (EditText) findViewById(R.id.editMessage);
        t6.setTypeface(type);
        TextView t7 = (TextView) findViewById(R.id.textView3);
        SpannableString content = new SpannableString("Preset Commands");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        t7.setText(content);
        t7.setTypeface(type, Typeface.BOLD);
        SpannableString content1 = new SpannableString("Personal Commands");
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        TextView t8 = (TextView) findViewById(R.id.textView6);
        t8.setText(content1);
        t8.setTypeface(type, Typeface.BOLD);
    }


}

