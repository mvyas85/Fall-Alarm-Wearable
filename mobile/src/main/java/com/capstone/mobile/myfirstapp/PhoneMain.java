package com.capstone.mobile.myfirstapp;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;
import com.capstone.mobile.R;
import com.capstone.data.DeviceData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.capstone.mobile.msg.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class PhoneMain extends Activity implements DataApi.DataListener,LocationListener {
    TextView x1, y1, z1,x2,y2,z2,loc_x,loc_y;

    //String oldX1,oldY1,oldZ1,oldX2,oldY2,oldZ2;

    Context context ;

    private DeviceData dataMsg;
    private BroadcastReceiver mLocalMessageReceiver;
    private WearableConnector mWearableConnector;
    private static final String TAG = "PhoneMainActivity";
    public static final String PATH = "/start/DataSocket";

    private LocationManager locationManager;
    private String provider;
    private double lat,lng;

    public PhoneMain(){}

    //GoogleApiClient googleApiClient;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_main);
        context = getApplicationContext();

        initWearableConnector();

        x1=(TextView)findViewById(R.id.x1); // create X axis object
        y1=(TextView)findViewById(R.id.y1); // create Y axis object
        z1=(TextView)findViewById(R.id.z1); // create Z axis object

        x2=(TextView)findViewById(R.id.x2); // create X axis object
        y2=(TextView)findViewById(R.id.y2); // create Y axis object
        z2=(TextView)findViewById(R.id.z2); // create Z axis object

        loc_x=(TextView)findViewById(R.id.loc_x);
        loc_y=(TextView)findViewById(R.id.loc_y);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);


        Log.i(TAG,"Provider created!");
        // Initialize the location fields
        if (location != null) {
            onLocationChanged(location);
            Log.i(TAG,"Location created!");
        } else {
            Log.i(TAG,"Location not available!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        locationManager.requestLocationUpdates(provider, 400, 1, this);
        mWearableConnector.connect();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        locationManager.removeUpdates(this);
        mWearableConnector.disconnect();
        super.onStop();
    }


    @Override
    public void onLocationChanged(Location location) {
        lat = (double) (location.getLatitude());
        lng = (double) (location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
            }
        }
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
        Log.i("Phone main","wearable connector is created");
        // Register a local broadcast receiver to handle messages that
        // have been received by the wearable message listening service.
        mLocalMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                dataMsg = (DeviceData) intent.getSerializableExtra("DeviceObj");

                x1.setText(""+dataMsg.getAccX());
                y1.setText("" + dataMsg.getAccY());
                z1.setText("" + dataMsg.getAccZ());
                x2.setText(""+dataMsg.getGyrX());
                y2.setText("" + dataMsg.getGyrY());
                z2.setText("" + dataMsg.getGyrZ());
                loc_x.setText("" + lat);
                loc_y.setText("" + lng);

                dataMsg.setLocX(lat);
                dataMsg.setLocY(lng);

                ConnectServer sendToServer = new ConnectServer(dataMsg);
                sendToServer.execute();
            }
        };
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocalMessageReceiver, messageFilter);
    }

    class ConnectServer  extends AsyncTask<Void, Void, Void> {

        DeviceData dataMsg;
        public ConnectServer(DeviceData dataMsg) {
            super();
            this.dataMsg =dataMsg;
        }

        @Override
        protected Void doInBackground(Void... params) {
            sendToServer();
            return null;
        }

        public void sendToServer(){
            Socket socket = null;
            ObjectOutputStream objOutputStream = null;
          //  DataInputStream dataInputStream = null;

            try
            {
                socket = new Socket("192.168.1.107", 5000); // IP address of your computer
                objOutputStream = new ObjectOutputStream(socket.getOutputStream());
                Log.i(TAG,"Currently i am sending "+dataMsg.getDeviceID());
                objOutputStream.writeObject(dataMsg);
                socket.close();
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (socket != null)
                {
                    try
                    {
                        socket.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                if (objOutputStream != null)
                {
                    try
                    {
                        objOutputStream.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}