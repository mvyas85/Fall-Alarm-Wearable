package com.capstone.wear.msg;


import android.content.Context;
import android.util.Log;

import com.capstone.data.DeviceData;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;


/**
 * Class to encapsulate connecting apps to wearable devices
 * and sending messages back and forth.
 */
public class WearableConnector {

    private static final String TAG = "WearableConeector";
    private GoogleApiClient mGoogleApiClient;

    /**
     * Constructor
     * @param ctx Context
     * @param connectionCallbacks add connection callbacks
     * @param connectionFailedListener connection failure callback
     */
    public WearableConnector(Context ctx,
                             GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                             GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {

        mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Connect mobile app & wearable app to one another.
     */
    public void connect() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Disconnect mobile app & wearable app.
     */
    public void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Add a message listener
     * @param listener MessageApi.MessageListener object
     */
    public void addListener(MessageApi.MessageListener listener) {
        if (mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.addListener(mGoogleApiClient, listener);
        }
    }

    /**
     * Remove a message listener
     * @param listener MessageApi.MessageListener object
     */
    public void removeListener(MessageApi.MessageListener listener) {
        if (mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.removeListener(mGoogleApiClient, listener);
        }
    }

    /**
     * Send a message to connected devices.
     * @param path Message path.
     * @param message Message to send.
     */
    public void sendMessage(String path, DeviceData message) {
        if (mGoogleApiClient.isConnected()) {
        	Log.i(TAG,"Google Api is connected");
            new WearableMessageSender(path, message, mGoogleApiClient).start();
        } else {
            Log.w(TAG, "Attempted to send message when not connected to Google API client.");
        }
    }

}