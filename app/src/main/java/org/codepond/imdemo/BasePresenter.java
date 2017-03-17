package org.codepond.imdemo;

import org.codepond.imdemo.service.chat.MessagingService;

public interface BasePresenter {
    void start(MessagingService messagingService);
    void stop();
}
