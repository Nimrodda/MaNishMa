package org.codepond.imdemo.service.chat;

import dagger.Component;

@Component(modules = MessagingServiceModule.class)
public interface MessagingServiceComponent {
    void inject(ChatService chatService);
}
