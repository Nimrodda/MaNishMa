package org.codepond.imdemo.chat;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;

import org.codepond.imdemo.ChatMessage;
import org.codepond.imdemo.R;
import org.codepond.imdemo.service.chat.MessagingService;

public class ChatViewModel implements MessagingService.OnMessageReceivedListener {
    public ObservableField<String> messageText = new ObservableField<>();
    private final String mUserId;
    private final String mChatId;
    private final ObservableArrayList<MessageViewModel> mMessages = new ObservableArrayList<>();
    private final MessagingService mMessagingService;

    public ChatViewModel(String userJid,
                        String participantJid,
                        MessagingService messagingService) {
        mUserId = userJid;
        mChatId = participantJid;
        mMessagingService = messagingService;
        mMessagingService.setOnMessageReceivedListener(this);
        messageText.set("");
    }

    void loadMessages() {
    }

    public void clickSend() {
        if (messageText.get() != null && messageText.get().length() > 0) {
            ChatMessage chatMessage = new ChatMessage(mUserId, messageText.get(), null);
            messageText.set("");
            if (mMessagingService != null) {
                mMessagingService.sendMessage(chatMessage);
            }
        }
    }

    public boolean isAuthorVisible(MessageViewModel model) {
        return isIncoming(model)
                && !isPreviousAuthorSame(model);
    }

    public int getBackground(MessageViewModel model) {
        if (isIncoming(model)) {
            if (isPreviousAuthorSame(model)) {
                return R.drawable.chat_bubble_incoming_ext;
            }
            else {
                return R.drawable.chat_bubble_incoming;
            }
        }
        else {
            if (isPreviousAuthorSame(model)) {
                return R.drawable.chat_bubble_outgoing_ext;
            }
            else {
                return R.drawable.chat_bubble_outgoing;
            }
        }
    }

    private boolean isPreviousAuthorSame(MessageViewModel model) {
        final int position = model.getPosition();
        return position > 0 && mMessages.get(position - 1).getAuthor().equals(model.getAuthor());
    }

    public boolean isIncoming(MessageViewModel model) {
        return !mUserId.equals(model.getAuthor());
    }

    @Override
    public void onMessageReceived(ChatMessage chatMessage) {
        mMessages.add(new MessageViewModel(chatMessage, mMessages.size()));
    }

    ObservableArrayList<MessageViewModel> getMessages() {
        return mMessages;
    }
}
