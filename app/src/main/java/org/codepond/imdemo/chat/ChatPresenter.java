package org.codepond.imdemo.chat;

import android.support.annotation.Nullable;

import org.codepond.imdemo.ChatMessage;
import org.codepond.imdemo.service.chat.MessagingService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

class ChatPresenter implements ChatContracts.Presenter, MessagingService.OnMessageReceivedListener {
    private final String mUserJid;
    private final ChatContracts.View mView;
    private final String mParticipantJid;
    private final List<ChatMessage> mMessages;
    @Nullable private MessagingService mMessagingService;

    @Inject
    ChatPresenter(ChatContracts.View view,
                  @Named("userJid") String userJid,
                  @Named("participantJid") String participantJid) {
        mView = view;
        mUserJid = userJid;
        mParticipantJid = participantJid;
        mMessages = new ArrayList<>();
    }

    @Override
    public void loadMessages() {
        // TODO: Load message history from DB
        mView.showMessages(mMessages);
    }

    @Override
    public void sendMessage(String message) {
        if (message != null && message.length() > 0) {
            ChatMessage chatMessage = new ChatMessage(mUserJid, mParticipantJid, message, false, System.currentTimeMillis());
            mMessages.add(chatMessage);
            mView.notifyNewMessageAdded();
            mView.cleanUserInput();
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
        mView.notifyNewMessageAdded();
    }

    List<ChatMessage> getMessages() {
        return mMessages;
    }

    @Override
    public void start(MessagingService messagingService) {
        if (messagingService == null) {
            throw new NullPointerException("messagingService must not be null!");
        }
        mMessagingService = messagingService;
        mMessagingService.setCurrentParticipant(mParticipantJid);
        mMessagingService.setOnMessageReceivedListener(this);
    }

    @Override
    public void stop() {
        if (mMessagingService != null) {
            mMessagingService.setOnMessageReceivedListener(null);
        }
    }
}
