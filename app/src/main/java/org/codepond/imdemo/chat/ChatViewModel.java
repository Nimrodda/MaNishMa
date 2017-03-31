package org.codepond.imdemo.chat;

import android.databinding.Observable;
import android.databinding.ObservableField;
import android.support.annotation.Nullable;

import org.codepond.imdemo.ChatMessage;
import org.codepond.imdemo.service.chat.MessagingService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class ChatViewModel implements MessagingService.OnMessageReceivedListener {
    public ObservableField<String> messageText = new ObservableField<>();
    private final String mUserJid;
    private final String mParticipantJid;
    private final List<ChatMessage> mMessages;
    @Nullable private MessagingService mMessagingService;

    public ChatViewModel(@Named("userJid") String userJid,
                  @Named("participantJid") String participantJid) {
        mUserJid = userJid;
        mParticipantJid = participantJid;
        mMessages = new ArrayList<>();
    }

    public void loadMessages() {
        // TODO: Load message history from DB
    }

    public void clickSend() {
        if (messageText.get() != null && messageText.get().length() > 0) {
            ChatMessage chatMessage = new ChatMessage(mUserJid, mParticipantJid, messageText.get(), false, System.currentTimeMillis());
            mMessages.add(chatMessage);
            messageText.set("");
            if (mMessagingService != null) {
                mMessagingService.sendMessage(chatMessage);
            }
            // TODO: store message in DB
        }
    }

    @Override
    public void onMessageReceived(ChatMessage chatMessage) {
        // TODO: store message in DB
        mMessages.add(chatMessage);
    }

    List<ChatMessage> getMessages() {
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
