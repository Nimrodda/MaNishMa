package org.codepond.imdemo.chat;

import android.support.annotation.Nullable;

import org.codepond.imdemo.ChatMessage;
import org.codepond.imdemo.MessagingService;

import java.util.ArrayList;
import java.util.List;

class ChatPresenter implements ChatContracts.Presenter, MessagingService.OnMessageReceivedListener {
    private final String mUserJid;
    private final ChatContracts.View mView;
    private final String mParticipantJid;
    private final List<ChatMessage> mMessages;
    @Nullable private MessagingService mMessagingService;

    ChatPresenter(ChatContracts.View view, String userJid, String participantJid) {
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
        if (mMessagingService == null) {
            throw new IllegalStateException("Cannot send message. mMessagingService is null!");
        }
        if (message != null && message.length() > 0) {
            ChatMessage chatMessage = new ChatMessage(mUserJid, mParticipantJid, message, false, System.currentTimeMillis());
            mMessages.add(chatMessage);
            mView.notifyNewMessageAdded();
            mView.cleanUserInput();
            mMessagingService.sendMessage(chatMessage);
            // TODO: store message in DB
        }
    }

    @Override
    public void setMessagingService(MessagingService service) {
        if (service != null) {
            service.setCurrentParticipant(mParticipantJid);
            service.setOnMessageReceivedListener(this);
        }
        mMessagingService = service;
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
}
