package org.codepond.imdemo;

import dagger.Component;

@Component(modules = ServiceModule.class)
public interface ServiceComponent {
    void inject(ChatService chatService);
}
