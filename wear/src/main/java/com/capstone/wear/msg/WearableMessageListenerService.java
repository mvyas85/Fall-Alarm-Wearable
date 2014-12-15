package com.capstone.wear.msg;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * WearableMessageListenerService listens for messages
 * and issues local broadcasts containing the new message data.
 */
public class WearableMessageListenerService extends WearableListenerService {

    private static final String TAG = "WearableMessageListenerService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d(TAG, "Incoming message...");

        // TODO Remove hard-coded message path

        if (messageEvent.getPath().equals("Some Msg"/*CamcorderRemoteConstants.MESSAGE_PATH*/)) {
            final String message = new String(messageEvent.getData());
            Log.d(TAG, "Message path: " + messageEvent.getPath());
            Log.d(TAG, "Message received: " + message);

            // Broadcast message to activity for handling
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("Some Extra"/*CamcorderRemoteConstants.MESSAGE_INTENT_EXTRA*/, message);
          //  LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}