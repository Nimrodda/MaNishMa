package org.codepond.imdemo;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "IMDEMO";
    public static final String ACTION_REMOTE_MSG = "org.codepond.imdemo.action.REMOTE_MSG";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived() called with: incomingMessage = [" + remoteMessage + "]");
        Intent intent = new Intent(ACTION_REMOTE_MSG);
        intent.putExtra("incomingMessage", remoteMessage);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
