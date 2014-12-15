package com.capstone.mobile.msg;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.capstone.data.DeviceData;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * WearableMessageListenerService listens for messages
 * and issues local broadcasts containing the new message data.
 */
public class WearableMessageListenerService extends WearableListenerService {

    private static final String TAG = "PhoneMessageListenerService";

    public static final String PATH = "/start/DataSocket";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d(TAG, "Incoming message...");
        // TODO Remove hard-coded message path
        if (messageEvent.getPath().equals(PATH)) {
            // Broadcast message to activity for handling

            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            ByteArrayInputStream b = new ByteArrayInputStream(messageEvent.getData());
            ObjectInputStream o = null;
            DeviceData data = null;

            try {
                o = new ObjectInputStream(b);
                data = (DeviceData)o.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            messageIntent.putExtra("DeviceObj", data);
            //messageIntent.putExtra("ByteArr",b);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}