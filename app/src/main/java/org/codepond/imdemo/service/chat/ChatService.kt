package org.codepond.imdemo.service.chat

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import android.util.Log

import javax.inject.Inject

class ChatService : Service() {
    inner class LocalBinder : Binder() {
        val service: MessagingService
            get() = this@ChatService.mMessagingServiceConnection
    }

    private val mBinder = LocalBinder()
    @Inject lateinit var mMessagingServiceConnection: MessagingServiceConnection

    override fun onCreate() {
        super.onCreate()
        DaggerMessagingServiceComponent.builder()
                .messagingServiceModule(MessagingServiceModule(applicationContext))
                .build()
                .inject(this)
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind() called with: intent = [$intent]")
        val username = intent.getStringExtra(EXTRA_USERNAME)
        val password = intent.getStringExtra(EXTRA_PASSWORD)
        mMessagingServiceConnection.start(username, password)
        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind() called with: intent = [$intent]")
        mMessagingServiceConnection.stop()
        return false
    }

    companion object {

        private val TAG = "ChatService"
        private val EXTRA_USERNAME = "extra_username"
        private val EXTRA_PASSWORD = "extra_password"

        fun bindService(context: Context, username: String, password: String, serviceConnection: ServiceConnection): Boolean {
            val intent = Intent(context, ChatService::class.java)
            intent.putExtra(EXTRA_USERNAME, username)
            intent.putExtra(EXTRA_PASSWORD, password)
            return context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }
}
