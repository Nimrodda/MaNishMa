package org.codepond.imdemo.service.chat;

import dagger.Component;

@Component(modules = MessagingServiceModule.class)
interface MessagingServiceComponent {
    void inject(ChatService chatService);
}
