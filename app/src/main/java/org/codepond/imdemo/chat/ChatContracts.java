package org.codepond.imdemo.chat;

import org.codepond.imdemo.ChatMessage;
import org.codepond.imdemo.MessagingService;

import java.util.List;

interface ChatContracts {
    interface Presenter {
        void sendMessage(String message);
        void setMessagingService(MessagingService service);
        void loadMessages();
    }

    interface View {
        void showMessages(List<ChatMessage> chatMessages);
        void notifyNewMessageAdded();
        void cleanUserInput();
    }
}
