package org.codepond.imdemo.service.chat

import org.codepond.imdemo.model.ChatMessage

interface MessagingService {
    interface OnMessageReceivedListener {
        fun onMessageReceived(chatMessage: ChatMessage)
    }

    fun sendMessage(chatMessage: ChatMessage)
    fun setCurrentParticipant(participantJid: String)
    fun setOnMessageReceivedListener(listener: OnMessageReceivedListener?)
}
