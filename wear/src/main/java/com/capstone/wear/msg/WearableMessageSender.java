package com.capstone.wear.msg;


import android.util.Log;

import com.capstone.data.DeviceData;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;

/**
 * WearableMessageSender is responsible for sending messages
 * between apps and wearable devices. It manages this work
 * on its own thread.
 */
public class WearableMessageSender extends Thread {

    private static final String TAG = "WearableMessageSender";

    private String mPath;
    private DeviceData mMessage;
    private GoogleApiClient mGoogleApiClient;
    /**
     * Constructor to send a message to the data layer
     * @param path Message path
     * @param msg Message contents
     * @param googleApiClient GoogleApiClient object
     */
    public WearableMessageSender(String path, DeviceData msg, GoogleApiClient googleApiClient) {

    	Log.i(TAG,"Google Api is connected");
        if (null == path || null == msg || null == googleApiClient) {
            Log.e(TAG, "Invalid parameter(s) passed to WearableMessageSender");
            throw new IllegalArgumentException("Invalid parameter(s) passed to WearableMessageSender");
        }
        mPath = path;
        mMessage = msg;
        mGoogleApiClient = googleApiClient;
    }

    public void run() {
        // Broadcast message to call connected nodes
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            MessageApi.SendMessageResult result = null;
            try {
                result = Wearable.MessageApi.sendMessage(
                        mGoogleApiClient,
                        node.getId(),
                        mPath,
                        DeviceData.serialize(mMessage)
                ).await();
                Log.i(TAG,"Trying to send byte arr::"+DeviceData.serialize(mMessage));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (result.getStatus().isSuccess()) {
                Log.d(TAG, "Message: {" + mMessage + "} successfully sent to: " + node.getDisplayName());
            } else {
                Log.e(TAG, "Failed to send message to device");
            }
        }
    }
}