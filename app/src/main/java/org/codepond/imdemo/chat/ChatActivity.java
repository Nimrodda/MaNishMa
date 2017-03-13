package org.codepond.imdemo.chat;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.codepond.imdemo.BaseActivity;
import org.codepond.imdemo.ChatMessage;
import org.codepond.imdemo.MessagingService;
import org.codepond.imdemo.R;
import org.codepond.imdemo.XmppConnectionService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends BaseActivity implements ChatContracts.View {
    public static final String EXTRA_PARTICIPANT_JID = "extra_participant_jid";
    private MessageAdapter mAdapter;
    private EditText mMessageText;
    private RecyclerView mRecyclerView;
    private String mParticipantJid;
    private ChatContracts.Presenter mPresenter;

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
        mAdapter = new MessageAdapter(new ArrayList<ChatMessage>());
        mRecyclerView.setAdapter(mAdapter);
        mMessageText = (EditText) findViewById(R.id.message_text);
        findViewById(R.id.button_send).setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                if (mMessageText.getText().length() > 0) {
                    String message = mMessageText.getText().toString();
                    mMessageText.setText("");
                    ChatMessage chatMessage = new ChatMessage("test@localhost", mParticipantJid, message, false, System.currentTimeMillis());
                    mPresenter.sendMessage(chatMessage);
                }
            }
        });
        mPresenter = new ChatPresenter(this);
    }

    @Override
    public void addMessage(ChatMessage message) {
        mAdapter.addMessage(message);
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MessagingService messagingService = ((XmppConnectionService.LocalBinder)service).getService();
        messagingService.setCurrentParticipant(mParticipantJid);
        mPresenter.setMessagingService(messagingService);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mPresenter.setMessagingService(null);
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
                    vh.author.setVisibility(android.view.View.VISIBLE);
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

        void addMessage(ChatMessage message) {
            mMessages.add(message);
            final int newMessagePosition = mMessages.size();
            mAdapter.notifyItemInserted(newMessagePosition);
        }

        class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView messageItem;
            TextView timestamp;
            TextView author;
            android.view.View container;
            MessageViewHolder(android.view.View view) {
                super(view);
                container = view.findViewById(R.id.message_container);
                messageItem = (TextView) view.findViewById(R.id.message_item);
                timestamp = (TextView) view.findViewById(R.id.timestamp);
                author = (TextView) view.findViewById(R.id.author);
            }
        }
    }
}
