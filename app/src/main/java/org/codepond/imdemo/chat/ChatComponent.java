package org.codepond.imdemo.chat;

import org.codepond.imdemo.ActivityScope;
import org.codepond.imdemo.ServiceComponent;

import dagger.Component;

@ActivityScope
@Component(dependencies = ServiceComponent.class, modules = ChatModule.class)
interface ChatComponent {
    void inject(ChatActivity chatActivity);
}
