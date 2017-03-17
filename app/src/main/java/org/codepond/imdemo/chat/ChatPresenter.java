package org.codepond.imdemo.chat;

import org.codepond.imdemo.ChatMessage;
import org.codepond.imdemo.MessagingService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

class ChatPresenter implements ChatContracts.Presenter, MessagingService.OnMessageReceivedListener {
    private final String mUserJid;
    private final ChatContracts.View mView;
    private final String mParticipantJid;
    private final List<ChatMessage> mMessages;
    private final MessagingService mMessagingService;

    @Inject
    ChatPresenter(ChatContracts.View view,
                  MessagingService messagingService,
                  @Named("userJid") String userJid,
                  @Named("participantJid") String participantJid) {
        mView = view;
        mUserJid = userJid;
        mParticipantJid = participantJid;
        mMessagingService = messagingService;
        mMessages = new ArrayList<>();
    }

    @Inject
    void setup() {
        mMessagingService.setCurrentParticipant(mParticipantJid);
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
            mMessagingService.sendMessage(chatMessage);
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
    public void start() {
        mMessagingService.setOnMessageReceivedListener(this);
    }

    @Override
    public void stop() {
        mMessagingService.setOnMessageReceivedListener(null);
    }
}
