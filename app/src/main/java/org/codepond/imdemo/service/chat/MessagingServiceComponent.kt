package org.codepond.imdemo.service.chat

import dagger.Component

@Component(modules = arrayOf(MessagingServiceModule::class))
interface MessagingServiceComponent {
    fun inject(chatService: ChatService)
}
