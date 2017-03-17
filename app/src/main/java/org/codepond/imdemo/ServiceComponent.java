package org.codepond.imdemo;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { AppModule.class, ServiceModule.class })
public interface ServiceComponent {
    void inject(ChatService chatService);
    MessagingService getMessagingService();
}
