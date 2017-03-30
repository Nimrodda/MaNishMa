package org.codepond.imdemo

import org.codepond.imdemo.service.chat.MessagingService

interface BasePresenter {
    fun start(messagingService: MessagingService)
    fun stop()
}
