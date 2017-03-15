package org.codepond.imdemo.chat;

import dagger.Component;

@Component(modules = ChatModule.class)
interface ChatComponent {
    void inject(ChatActivity chatActivity);
}
