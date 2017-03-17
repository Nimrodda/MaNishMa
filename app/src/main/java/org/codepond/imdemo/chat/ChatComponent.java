package org.codepond.imdemo.chat;

import org.codepond.imdemo.ActivityScope;

import dagger.Component;

@ActivityScope
@Component(modules = ChatModule.class)
interface ChatComponent {
    void inject(ChatActivity chatActivity);
}
