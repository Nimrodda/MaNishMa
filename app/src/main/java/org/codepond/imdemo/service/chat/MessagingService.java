package org.codepond.imdemo.service.chat;

import org.codepond.imdemo.ChatMessage;

public interface MessagingService {
    interface OnMessageReceivedListener {
        void onMessageReceived(ChatMessage chatMessage);
    }

    void sendMessage(ChatMessage chatMessage);
    void setCurrentParticipant(String participantJid);
    void setOnMessageReceivedListener(OnMessageReceivedListener listener);
}
