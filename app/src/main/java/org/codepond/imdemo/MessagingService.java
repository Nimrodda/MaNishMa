package org.codepond.imdemo;

public interface MessagingService {
    interface OnMessageReceivedListener {
        void onMessageReceived(ChatMessage chatMessage);
    }

    void start(String username, String password);
    void stop();
    void sendMessage(ChatMessage chatMessage);
    void setCurrentParticipant(String participantJid);
    void setOnMessageReceivedListener(OnMessageReceivedListener listener);
}
