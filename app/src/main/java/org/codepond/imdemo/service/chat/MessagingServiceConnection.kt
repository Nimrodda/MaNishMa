package org.codepond.imdemo.service.chat

interface MessagingServiceConnection : MessagingService {
    fun start(username: String, password: String)
    fun stop()
}
