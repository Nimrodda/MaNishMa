package org.codepond.imdemo.chat

import org.codepond.imdemo.ActivityScope

import dagger.Component

@ActivityScope
@Component(modules = arrayOf(ChatModule::class))
interface ChatComponent {
    fun inject(chatActivity: ChatActivity)
}
