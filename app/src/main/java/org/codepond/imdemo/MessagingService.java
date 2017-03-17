package org.codepond.imdemo;

public interface MessagingService {
    interface OnMessageReceivedListener {
        void onMessageReceived(ChatMessage chatMessage);
    }

    void sendMessage(ChatMessage chatMessage);
    void setCurrentParticipant(String participantJid);
    void setOnMessageReceivedListener(OnMessageReceivedListener listener);
}
