package org.codepond.imdemo;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class BaseActivity extends AppCompatActivity implements ServiceConnection {
    private static final String TAG = "BaseActivity";
    private boolean mBound;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        mBound = ChatService.bindService(this, "test", "123456", this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
        if (mBound) {
            Log.d(TAG, "onStop: unbindService");
            unbindService(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
}
