package org.codepond.imdemo.service.chat;

import org.codepond.imdemo.ChatMessage;

class FakeMessagingService implements MessagingServiceConnection {
    @Override
    public void start(String username, String password) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void sendMessage(ChatMessage chatMessage) {

    }

    @Override
    public void setCurrentParticipant(String participantJid) {

    }

    @Override
    public void setOnMessageReceivedListener(OnMessageReceivedListener listener) {

    }
}
