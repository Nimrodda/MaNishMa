package org.codepond.imdemo;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.codepond.imdemo.chat.ChatActivity;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class XmppConnectionService extends Service implements MessagingService {
    public class LocalBinder extends Binder {
        public XmppConnectionService getService() {
            return XmppConnectionService.this;
        }
    }

    private class MainHandler extends Handler {
        private MainHandler(Looper looper) {
            super(looper);
        }

        private static final int MSG_RECEIVED = 1;

        @Override
        public void handleMessage(Message msg) {
            if (mOnMessageReceivedListener != null) {
                switch (msg.what) {
                    case MSG_RECEIVED:
                        mOnMessageReceivedListener.onMessageReceived((ChatMessage) msg.obj);
                        break;
                }
            }
        }
    }

    private ChatMessageListener mChatMessageListener = new ChatMessageListener() {
        @Override
        public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message packet) {
            if (mBound && packet != null && packet.getBody() != null) {
                ChatMessage chatMessage = new ChatMessage(packet.getFrom(), packet.getTo(), packet.getBody(), true, System.currentTimeMillis());
                if (packet.getFrom().equals(mCurrentParticipant)) {
                    notifyMessageReceived(chatMessage);
                }
                else {
                    // TODO: Persist to local storage

                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra(ChatActivity.EXTRA_PARTICIPANT_JID, packet.getFrom());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notification = new NotificationCompat.Builder(getApplicationContext())
                            .setContentTitle(packet.getFrom())
                            .setContentText(packet.getBody())
                            .setSmallIcon(R.drawable.input_circle)
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setAutoCancel(true)
                            .build();
                    NotificationManagerCompat.from(getApplicationContext()).notify(0, notification);
                }
            }
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isConnected()) {
                if (!mConnection.isConnected()) {
                    connect();
                }
                if (!mMessageQueue.isEmpty()) {
                    processMessages();
                }
            }
            else if (mConnection.isConnected()) {
                disconnect();
            }
        }
    };

    private static final String EXTRA_USERNAME = "extra_username";
    private static final String EXTRA_PASSWORD = "extra_password";

    private static final String TAG = "XmppConnectionService";
    private final IBinder mBinder = new LocalBinder();
    private Handler mWorkerHandler;
    private Handler mMainHandler;
    private HandlerThread mHandlerThread;
    private AbstractXMPPConnection mConnection;
    private boolean mBound;
    private Queue<ChatMessage> mMessageQueue = new LinkedList<>();
    private Map<String, String> mChatThreads = new HashMap<>();
    private String mCurrentParticipant;
    private OnMessageReceivedListener mOnMessageReceivedListener;

    public static boolean bindService(Context context, String username, String password, ServiceConnection serviceConnection) {
        Intent intent = new Intent(context, XmppConnectionService.class);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.putExtra(EXTRA_PASSWORD, password);
        return context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() called");
        mHandlerThread = new HandlerThread("XmppConnection");
        mHandlerThread.start();
        mWorkerHandler = new Handler(mHandlerThread.getLooper());
        mMainHandler = new MainHandler(Looper.getMainLooper());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() called with: intent = [" + intent + "]");
        String username = intent.getStringExtra(EXTRA_USERNAME);
        String password = intent.getStringExtra(EXTRA_PASSWORD);
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(username, password)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible)
                .setPort(5222)
                .setDebuggerEnabled(true)
                .setServiceName("localhost")
                .setHost("10.0.2.2")
                .setSendPresence(true)
                .build();

        mConnection = new XMPPTCPConnection(config);
        registerReceiver(mReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        mBound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind() called with: intent = [" + intent + "]");
        disconnect();
        mHandlerThread.quitSafely();
        unregisterReceiver(mReceiver);
        mBound = false;
        return false;
    }

    @Override
    public void sendMessage(ChatMessage chatMessage) {
        mMessageQueue.offer(chatMessage);
        processMessages();
    }

    @Override
    public void setCurrentParticipant(String jid) {
        mCurrentParticipant = jid;
    }

    @Override
    public void setOnMessageReceivedListener(OnMessageReceivedListener listener) {
        mOnMessageReceivedListener = listener;
    }

    private void connect() {
        mWorkerHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mConnection.connect();
                    Log.v(TAG, "Connected");
                    mConnection.login();
                    ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
                    chatManager.addChatListener(new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean createdLocally) {
                            mChatThreads.put(chat.getParticipant(), chat.getThreadID());
                            if (!createdLocally) {
                                chat.addMessageListener(mChatMessageListener);
                            }
                        }
                    });
                } catch (SmackException | IOException | XMPPException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void disconnect() {
        mWorkerHandler.post(new Runnable() {
            @Override
            public void run() {
                mConnection.disconnect();
            }
        });
    }

    private void processMessages() {
        mWorkerHandler.post(new Runnable() {
            @Override
            public void run() {
                while (isConnected() && !mMessageQueue.isEmpty()) {
                    try {
                        ChatMessage chatMessage = mMessageQueue.peek();
                        ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
                        Chat chat;
                        if (mChatThreads.containsKey(chatMessage.to)) {
                            chat = chatManager.getThreadChat(mChatThreads.get(chatMessage.to));
                        }
                        else {
                            chat = chatManager.createChat(chatMessage.to, mChatMessageListener);
                        }
                        Log.v(TAG, "Sending message");
                        chat.sendMessage(chatMessage.messageText);
                        mMessageQueue.poll();
                    } catch (SmackException.NotConnectedException e) {
                        Log.v(TAG, "Failed to send chat message, not connected");
                        break;
                    }
                }
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void notifyMessageReceived(final ChatMessage chatMessage) {
        Message message = mMainHandler.obtainMessage(MainHandler.MSG_RECEIVED);
        message.obj = chatMessage;
        mMainHandler.sendMessage(message);
    }
}
