package org.codepond.imdemo.chat;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;

import org.codepond.imdemo.ChatMessage;
import org.codepond.imdemo.R;
import org.codepond.imdemo.service.chat.MessagingService;

import javax.inject.Named;

public class ChatViewModel implements MessagingService.OnMessageReceivedListener {
    public ObservableField<String> messageText = new ObservableField<>();
    private final String mUserJid;
    private final String mParticipantJid;
    private final ObservableArrayList<MessageViewModel> mMessages = new ObservableArrayList<>();
    @Nullable private MessagingService mMessagingService;

    public ChatViewModel(@Named("userJid") String userJid,
                  @Named("participantJid") String participantJid) {
        mUserJid = userJid;
        mParticipantJid = participantJid;
        messageText.set("");
    }

    void loadMessages() {
        // TODO: Load message history from DB
    }

    public void clickSend() {
        if (messageText.get() != null && messageText.get().length() > 0) {
            ChatMessage chatMessage = new ChatMessage(mUserJid, mParticipantJid, messageText.get(), false, System.currentTimeMillis());
            mMessages.add(new MessageViewModel(chatMessage, mMessages.size()));
            messageText.set("");
            if (mMessagingService != null) {
                mMessagingService.sendMessage(chatMessage);
            }
            // TODO: store message in DB
        }
    }

    public boolean isAuthorVisible(MessageViewModel model) {
        return model.getIncoming()
                && !isPreviousAuthorSame(model);
    }

    public int getBackground(MessageViewModel model) {
        if (model.getIncoming()) {
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

    @Override
    public void onMessageReceived(ChatMessage chatMessage) {
        // TODO: store message in DB
        mMessages.add(new MessageViewModel(chatMessage, mMessages.size()));
    }

    ObservableArrayList<MessageViewModel> getMessages() {
        return mMessages;
    }

    void start(MessagingService messagingService) {
        if (messagingService == null) {
            throw new NullPointerException("messagingService must not be null!");
        }
        mMessagingService = messagingService;
        mMessagingService.setCurrentParticipant(mParticipantJid);
        mMessagingService.setOnMessageReceivedListener(this);
    }

    void stop() {
        if (mMessagingService != null) {
            mMessagingService.setOnMessageReceivedListener(null);
        }
    }
}
