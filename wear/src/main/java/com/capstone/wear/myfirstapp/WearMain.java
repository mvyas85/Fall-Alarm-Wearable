package com.capstone.wear.myfirstapp;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.capstone.data.DeviceData;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;

import com.capstone.wear.msg.*;

public class WearMain extends Activity implements SensorEventListener {

    private SensorManager sensorManager;

    TextView x1; // declare X axis object
    TextView y1; // declare Y axis object
    TextView z1; // declare Z axis object

    TextView x2; // declare X axis object
    TextView y2; // declare Y axis object
    TextView z2; // declare Z axis object

    String x1Str, y1Str, z1Str, x2Str, y2Str, z2Str;
    String oldX1, oldY1, oldZ1, oldX2, oldY2, oldZ2;

    Button startContinuous;
    private boolean startStop = false, valueChanged = true;

    Context context;

    private BroadcastReceiver mLocalMessageReceiver;
    private WearableConnector mWearableConnector;
    private static final String TAG = "WearMainActivity";
    public static final String PATH = "/start/DataSocket";

    public static final int INTERVAL = 250;

    public WearMain() {
    }

    public WearMain(String x1Str, String y1Str, String z1Str, String x2Str,
                    String y2Str, String z2Str) {
        super();
        this.x1Str = x1Str;
        this.y1Str = y1Str;
        this.z1Str = z1Str;
        this.x2Str = x2Str;
        this.y2Str = y2Str;
        this.z2Str = z2Str;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rect_activity_wear_main);

        context = getApplicationContext();
        initWearableConnector();

        x1 = (TextView) findViewById(R.id.x1); // create X axis object
        y1 = (TextView) findViewById(R.id.y1); // create Y axis object
        z1 = (TextView) findViewById(R.id.z1); // create Z axis object

        x2 = (TextView) findViewById(R.id.x2); // create X axis object
        y2 = (TextView) findViewById(R.id.y2); // create Y axis object
        z2 = (TextView) findViewById(R.id.z2); // create Z axis object

        startContinuous = (Button) findViewById(R.id.startContinuous);

        startContinuous.setOnClickListener(buttonContinuousClickListener);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // add listener. The listener will be HelloAndroid (this) class
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    class SendAllData extends Thread{
         public void run() {
            if(!Thread.currentThread().isInterrupted())
                getAllDataSendMsg();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mWearableConnector.connect();
    }

    @Override
    protected void onStop() {
        mWearableConnector.disconnect();
        Log.d(TAG, "onStop");
        super.onStop();
    }

    /**
     * Initialize the wearable connector which will
     * facilitate communication to mobile devices.
     */
    private void initWearableConnector() {

        // Setup wearable connection callbacks
        GoogleApiClient.OnConnectionFailedListener wearableConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult result) {
                Log.d(TAG, "onConnectionFailed: " + result);
            }
        };
        GoogleApiClient.ConnectionCallbacks wearableConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle connectionHint) {
                Log.d(TAG, "onConnected: ");//Do stuff
            }

            @Override
            public void onConnectionSuspended(int cause) {
                Log.d(TAG, "onConnectionSuspended: " + cause);
            }
        };
        mWearableConnector = new WearableConnector(
                this,
                wearableConnectionCallbacks,
                wearableConnectionFailedListener);

        // Register a local broadcast receiver to handle messages that
        // have been received by the wearable message listening service.
        mLocalMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("Some Msgee");
                Log.d(TAG, "Message received from app: " + message);
                // Update UI and onClick behavior based upon the recording state received from the mobile app
                //mRecordingStateContext.changeState(message);
                // mCamcorderRemotePagerAdapter.notifyDataSetChanged(); // refresh UI
            }
        };
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalMessageReceiver, messageFilter);

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        // check sensor type
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            oldX1 = x1.getText().toString();
            oldY1 = y1.getText().toString();
            oldZ1 = z1.getText().toString();

            // assign directions/
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            x1.setText("" + x);
            y1.setText("" + y);
            z1.setText("" + z);

        }
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            oldX2 = x2.getText().toString();
            oldY2 = y2.getText().toString();
            oldZ2 = z2.getText().toString();

            // assign directions/
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            x2.setText("" + x);
            y2.setText("" + y);
            z2.setText("" + z);
        }

        if (x1.getText().toString().equals(oldX1) && y1.getText().toString().equals(oldY1)
                && z1.getText().toString().equals(oldZ1) && x2.getText().toString().equals(oldX2)
                && y2.getText().toString().equals(oldY2) && z2.getText().toString().equals(oldZ2)) {
            valueChanged = false;
        } else {
            valueChanged = true;
        }
    }

    private ScheduledExecutorService executor;

    Button.OnClickListener buttonContinuousClickListener = new Button.OnClickListener() {
        public void onClick(View arg0) {

            if (startStop) {
                startStop = false;
                startContinuous.setText("Send Continuous");
                startContinuous.setBackgroundColor(Color.GREEN);

                executor.shutdownNow();
                Log.i(TAG,"Checking for shutdown "+ executor.isShutdown());
            }
            else{
                startStop = true;
                startContinuous.setText("Stop Continuous");
                startContinuous.setBackgroundColor(Color.RED);

                getAllDataSendMsg();
                executor = Executors.newScheduledThreadPool(1);
               executor.scheduleAtFixedRate(new SendAllData(), 0, INTERVAL, TimeUnit.MILLISECONDS);
            }
        }
    };

    public void getAllDataSendMsg(){
        DeviceData dd = new DeviceData("PID1001"
                , Double.parseDouble(x1.getText().toString())
                , Double.parseDouble(y1.getText().toString())
                , Double.parseDouble(z1.getText().toString())
                , Double.parseDouble(x2.getText().toString())
                , Double.parseDouble(y2.getText().toString())
                , Double.parseDouble(z2.getText().toString())
                , 0
                , 0);

        mWearableConnector.sendMessage(PATH, dd);
    }
}
