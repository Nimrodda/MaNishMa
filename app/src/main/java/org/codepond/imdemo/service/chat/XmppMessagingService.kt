package org.codepond.imdemo.service.chat

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.NotificationCompat
import android.util.Log
import org.codepond.imdemo.ChatMessage
import org.codepond.imdemo.R
import org.codepond.imdemo.chat.ChatActivity
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.ConnectionConfiguration
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.chat.Chat
import org.jivesoftware.smack.chat.ChatManager
import org.jivesoftware.smack.chat.ChatMessageListener
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import java.io.IOException
import java.util.*
import javax.inject.Inject

class XmppMessagingService @Inject constructor(private val context: Context) : MessagingServiceConnection {
    private val TAG = "XmppConnectionService"
    private val MSG_RECEIVED = 1

    private inner class MainHandler constructor(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_RECEIVED -> mOnMessageReceivedListener?.onMessageReceived(msg.obj as ChatMessage)
            }
        }
    }

    private val mChatMessageListener = ChatMessageListener { chat, packet ->
        if (mBound && packet != null && packet.body != null) {
            val chatMessage = ChatMessage(packet.from, packet.to, packet.body, true, System.currentTimeMillis())
            if (packet.from == mCurrentParticipant) {
                notifyMessageReceived(chatMessage)
            } else {
                // TODO: Persist to local storage

                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra(ChatActivity.EXTRA_PARTICIPANT_JID, packet.from)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val notification = NotificationCompat.Builder(context)
                        .setContentTitle(packet.from)
                        .setContentText(packet.body)
                        .setSmallIcon(R.drawable.input_circle)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setAutoCancel(true)
                        .build()
                NotificationManagerCompat.from(context).notify(0, notification)
            }
        }
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (isConnected) {
                if (!mConnection.isConnected) {
                    connect()
                }
                if (!mMessageQueue.isEmpty()) {
                    processMessages()
                }
            } else if (mConnection.isConnected) {
                disconnect()
            }
        }
    }
    private val mWorkerHandler: Handler
    private val mMainHandler = MainHandler(Looper.getMainLooper())
    private val mHandlerThread = HandlerThread("XmppConnection")
    private lateinit var mConnection: AbstractXMPPConnection
    private val mMessageQueue = LinkedList<ChatMessage>()
    private val mChatThreads = HashMap<String, String>()
    private var mCurrentParticipant: String? = null
    private var mOnMessageReceivedListener: MessagingService.OnMessageReceivedListener? = null
    private var mBound: Boolean = false

    init {
        mHandlerThread.start()
        mWorkerHandler = Handler(mHandlerThread.looper)
    }

    override fun start(username: String, password: String) {
        val config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(username, password)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible)
                .setPort(5222)
                .setDebuggerEnabled(true)
                .setServiceName("localhost")
                .setHost("10.0.2.2")
                .setSendPresence(true)
                .build()

        mConnection = XMPPTCPConnection(config)
        context.registerReceiver(mReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        mBound = true
    }

    override fun stop() {
        mBound = false
        disconnect()
        mHandlerThread.quitSafely()
        context.unregisterReceiver(mReceiver)
    }

    override fun sendMessage(chatMessage: ChatMessage) {
        mMessageQueue.offer(chatMessage)
        processMessages()
    }

    override fun setCurrentParticipant(participantJid: String) {
        mCurrentParticipant = participantJid
    }

    override fun setOnMessageReceivedListener(listener: MessagingService.OnMessageReceivedListener?) {
        mOnMessageReceivedListener = listener
    }

    private fun connect() {
        mWorkerHandler.post {
            try {
                mConnection.connect()
                Log.v(TAG, "Connected")
                mConnection.login()
                val chatManager = ChatManager.getInstanceFor(mConnection)
                chatManager.addChatListener { chat, createdLocally ->
                    mChatThreads.put(chat.participant, chat.threadID)
                    if (!createdLocally) {
                        chat.addMessageListener(mChatMessageListener)
                    }
                }
            } catch (e: SmackException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: XMPPException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        mWorkerHandler.post { mConnection.disconnect() }
    }

    private fun processMessages() {
        mWorkerHandler.post {
            while (isConnected && !mMessageQueue.isEmpty()) {
                try {
                    val message = mMessageQueue.peek()
                    val chatManager = ChatManager.getInstanceFor(mConnection)
                    val chat: Chat
                    if (mChatThreads.containsKey(message.to)) {
                        chat = chatManager.getThreadChat(mChatThreads[message.to])
                    } else {
                        chat = chatManager.createChat(message.to, mChatMessageListener)
                    }
                    Log.v(TAG, "Sending message")
                    chat.sendMessage(message.messageText)
                    mMessageQueue.poll()
                } catch (e: SmackException.NotConnectedException) {
                    Log.v(TAG, "Failed to send chat message, not connected")
                    break
                }

            }
        }
    }

    private val isConnected: Boolean
        get() {
            val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
        }

    private fun notifyMessageReceived(chatMessage: ChatMessage) {
        val message = mMainHandler.obtainMessage(MSG_RECEIVED)
        message.obj = chatMessage
        mMainHandler.sendMessage(message)
    }
}
