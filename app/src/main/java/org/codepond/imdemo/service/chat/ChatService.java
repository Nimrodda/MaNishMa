package org.codepond.imdemo.service.chat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import javax.inject.Inject;

public class ChatService extends Service {
    public class LocalBinder extends Binder {
        public MessagingService getService() {
            return ChatService.this.mMessagingServiceConnection;
        }
    }

    private static final String TAG = "ChatService";
    private static final String EXTRA_USERNAME = "extra_username";
    private static final String EXTRA_PASSWORD = "extra_password";

    private final IBinder mBinder = new LocalBinder();
    @Inject MessagingServiceConnection mMessagingServiceConnection;

    public static boolean bindService(Context context, String username, String password, ServiceConnection serviceConnection) {
        Intent intent = new Intent(context, ChatService.class);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_PASSWORD, password);
        return context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerMessagingServiceComponent.builder()
                .messagingServiceModule(new MessagingServiceModule(getApplicationContext()))
                .build()
                .inject(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() called with: intent = [" + intent + "]");
        String username = intent.getStringExtra(EXTRA_USERNAME);
        String password = intent.getStringExtra(EXTRA_PASSWORD);
        mMessagingServiceConnection.start(username, password);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() called with: intent = [" + intent + "]");
        mMessagingServiceConnection.stop();
        return false;
    }
}
