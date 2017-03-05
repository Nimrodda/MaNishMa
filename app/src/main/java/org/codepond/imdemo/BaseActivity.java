package org.codepond.imdemo;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection, XmppConnectionService.XmppEventListener {
    private static final String TAG = "BaseActivity";
    private XmppConnectionService mService;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        XmppConnectionService.bindService(this, "test", "123456", this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
        if (mService != null) {
            Log.d(TAG, "onStop: unbindService");
            unbindService(this);
            mService = null;
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d(TAG, "onServiceConnected() called with: componentName = [" + componentName + "], iBinder = [" + iBinder + "]");
        mService = ((XmppConnectionService.LocalBinder)iBinder).getService();
        mService.setXmppEventListener(this);
        onServiceConnected(mService);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.d(TAG, "onServiceDisconnected() called with: componentName = [" + componentName + "]");
        mService.setXmppEventListener(null);
        mService = null;
    }

    protected void onServiceConnected(XmppConnectionService service) {
    }
}
