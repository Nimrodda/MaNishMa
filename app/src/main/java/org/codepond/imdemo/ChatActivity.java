package org.codepond.imdemo;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.RemoteMessage;

import org.jivesoftware.smack.SmackException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends BaseActivity {
    public static final String EXTRA_PARTICIPANT_JID = "extra_participant_jid";
    private List<ChatMessage> mMessages;
    private MessageAdapter mAdapter;
    private EditText mMessageText;
    private RecyclerView mRecyclerView;
    private Map<String, String> mMessageData = new HashMap<>();
    private String mParticipantJid;
    @Nullable private XmppConnectionService mService;

    private BroadcastReceiver mRec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RemoteMessage remoteMessage = intent.getParcelableExtra("incomingMessage");
            Map<String, String> data = remoteMessage.getData();
//            ChatMessage chatMessage = new ChatMessage(data.get("from"), data.get("text"), true, Long.valueOf(data.get("timestamp")));
//            addMessage(chatMessage);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParticipantJid = getIntent().getStringExtra(EXTRA_PARTICIPANT_JID);
        setContentView(R.layout.activity_chat);
        mRecyclerView = (RecyclerView) findViewById(R.id.message_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMessages = new ArrayList<>();
        mAdapter = new MessageAdapter(mMessages);
        mRecyclerView.setAdapter(mAdapter);
        mMessageText = (EditText) findViewById(R.id.message_text);
        findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMessageText.getText().length() > 0) {
                    String message = mMessageText.getText().toString();
                    mMessageText.setText("");
                    ChatMessage chatMessage = new ChatMessage("test@localhost", mParticipantJid, message, false, System.currentTimeMillis());
                    addMessage(chatMessage);
                    try {
                        sendMessage(chatMessage);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRec, new IntentFilter(MyFirebaseMessagingService.ACTION_REMOTE_MSG));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRec);
    }

    private void addMessage(ChatMessage message) {
        mMessages.add(message);
        mAdapter.notifyItemInserted(mMessages.size());
        mRecyclerView.smoothScrollToPosition(mMessages.size());
    }


    protected void sendMessage(ChatMessage chatMessage) throws SmackException.NotConnectedException {
        if (mService != null) {
            mService.sendMessage(chatMessage);
        }
    }

    @Override
    public void onMessageReceived(ChatMessage chatMessage) {
        addMessage(chatMessage);
    }

    @Override
    public void onServiceConnected(XmppConnectionService service) {
        mService = service;
        mService.setCurrentParticipant(mParticipantJid);
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
        private final List<ChatMessage> mMessages;

        MessageAdapter(List<ChatMessage> messages) {
            mMessages = messages;
        }

        @Override
        public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new MessageViewHolder(inflater.inflate(R.layout.message_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MessageAdapter.MessageViewHolder vh, int position) {
            ChatMessage chatMessage = mMessages.get(position);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) vh.container.getLayoutParams();
            if (chatMessage.incomingMessage) {
                if (isPreviousAuthorSame(position, chatMessage.from)) {
                    vh.container.setBackgroundResource(R.drawable.chat_bubble_incoming_ext);
                }
                else {
                    vh.author.setText(chatMessage.from);
                    vh.author.setVisibility(View.VISIBLE);
                    vh.container.setBackgroundResource(R.drawable.chat_bubble_incoming);

                }
                lp.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
            }
            else {
                if (isPreviousAuthorSame(position, chatMessage.from)) {
                    vh.container.setBackgroundResource(R.drawable.chat_bubble_outgoing_ext);
                }
                else {
                    vh.container.setBackgroundResource(R.drawable.chat_bubble_outgoing);
                }
                lp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            }
            vh.container.setLayoutParams(lp);
            vh.messageItem.setText(chatMessage.messageText);
            vh.timestamp.setText(SimpleDateFormat.getInstance().format(chatMessage.timestamp));
        }

        private boolean isPreviousAuthorSame(int position, String displayName) {
            return position > 0 && mMessages.get(position - 1).from.equals(displayName);
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }

        class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView messageItem;
            TextView timestamp;
            TextView author;
            View container;
            MessageViewHolder(View view) {
                super(view);
                container = view.findViewById(R.id.message_container);
                messageItem = (TextView) view.findViewById(R.id.message_item);
                timestamp = (TextView) view.findViewById(R.id.timestamp);
                author = (TextView) view.findViewById(R.id.author);
            }
        }
    }
}
