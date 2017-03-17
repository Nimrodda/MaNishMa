package org.codepond.imdemo.chat;

import org.codepond.imdemo.BasePresenter;
import org.codepond.imdemo.ChatMessage;

import java.util.List;

interface ChatContracts {
    interface Presenter extends BasePresenter {
        void sendMessage(String message);
        void loadMessages();
    }

    interface View {
        void showMessages(List<ChatMessage> chatMessages);
        void notifyNewMessageAdded();
        void cleanUserInput();
    }
}
