package org.codepond.imdemo

import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.util.Log

import org.codepond.imdemo.service.chat.ChatService

abstract class BaseActivity : AppCompatActivity(), ServiceConnection {
    private var mBound: Boolean = false

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
        mBound = ChatService.bindService(this, "test", "123456", this)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
        if (mBound) {
            Log.d(TAG, "onStop: unbindService")
            unbindService(this)
        }
    }

    companion object {
        private val TAG = "BaseActivity"
    }
}
