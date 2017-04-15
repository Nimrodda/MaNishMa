package org.codepond.imdemo.chat;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.codepond.imdemo.BaseActivity;
import org.codepond.imdemo.R;
import org.codepond.imdemo.databinding.ActivityChatBinding;
import org.codepond.imdemo.databinding.MessageItemBinding;
import org.codepond.imdemo.service.chat.FirebaseMessagingService;

public class ChatActivity extends BaseActivity {
    public static final String USER_ID = "extra_participant_jid";
    ChatViewModel mChatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userId = getIntent().getStringExtra(USER_ID);
        mChatViewModel = new ChatViewModel(userId, "", new FirebaseMessagingService());
        ActivityChatBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        binding.setModel(mChatViewModel);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.messageList.setLayoutManager(layoutManager);
        binding.messageList.setItemAnimator(new DefaultItemAnimator());
        binding.messageList.setAdapter(new MessageAdapter(mChatViewModel.getMessages()));
        mChatViewModel.loadMessages();
    }

    @BindingAdapter("remote")
    public static void remote(View view, boolean isRemote) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (isRemote) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        }
        else {
            lp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        }
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
        @NonNull private ObservableArrayList<MessageViewModel> mMessages;
        private RecyclerView mRecyclerView;

        MessageAdapter(@NonNull ObservableArrayList<MessageViewModel> messages) {
            mMessages = messages;
        }

        @Override
        public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new MessageViewHolder(MessageItemBinding.inflate(inflater, parent, false));
        }

        @Override
        public void onBindViewHolder(MessageAdapter.MessageViewHolder vh, int position) {
            MessageViewModel messageViewModel = mMessages.get(position);
            vh.bind(messageViewModel, mChatViewModel);
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            mRecyclerView = recyclerView;
            mMessages.addOnListChangedCallback(mOnListChangedCallback);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            mMessages.removeOnListChangedCallback(mOnListChangedCallback);
        }

        private ObservableArrayList.OnListChangedCallback mOnListChangedCallback = new ObservableArrayList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList observableList) {

            }

            @Override
            public void onItemRangeChanged(ObservableList observableList, int i, int i1) {

            }

            @Override
            public void onItemRangeInserted(ObservableList observableList, int i, int i1) {
                final int newMessagePosition = mMessages.size();
                notifyItemInserted(newMessagePosition);
                mRecyclerView.smoothScrollToPosition(getItemCount());
            }

            @Override
            public void onItemRangeMoved(ObservableList observableList, int i, int i1, int i2) {

            }

            @Override
            public void onItemRangeRemoved(ObservableList observableList, int i, int i1) {

            }
        };

        class MessageViewHolder extends RecyclerView.ViewHolder {
            MessageItemBinding binding;
            MessageViewHolder(MessageItemBinding messageItemBinding) {
                super(messageItemBinding.getRoot());
                binding = messageItemBinding;
            }

            void bind(MessageViewModel messageViewModel, ChatViewModel chatViewModel) {
                binding.setMessage(messageViewModel);
                binding.setChat(chatViewModel);
                binding.setContext(getApplicationContext());
                binding.executePendingBindings();
            }
        }
    }
}
