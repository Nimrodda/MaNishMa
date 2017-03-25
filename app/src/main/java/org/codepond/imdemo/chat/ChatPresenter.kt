package org.codepond.imdemo.chat

import org.codepond.imdemo.ChatMessage
import org.codepond.imdemo.service.chat.MessagingService

import java.util.ArrayList

import javax.inject.Inject
import javax.inject.Named

class ChatPresenter
@Inject
constructor(private val view: ChatContracts.View,
            @Named("userJid") private val userJid: String,
            @Named("participantJid") private val participantJid: String) : ChatContracts.Presenter, MessagingService.OnMessageReceivedListener {

    val messages: MutableList<ChatMessage> = ArrayList()

    private var messagingService: MessagingService? = null

    override fun loadMessages() {
        // TODO: Load message history from DB
        view.showMessages(messages)
    }

    override fun sendMessage(message: String) {
        if (message.isNotEmpty()) {
            val chatMessage = ChatMessage(userJid, participantJid, message, false, System.currentTimeMillis())
            messages.add(chatMessage)
            view.notifyNewMessageAdded()
            view.cleanUserInput()
            messagingService?.sendMessage(chatMessage)
            // TODO: store message in DB
        }
    }

    override fun onMessageReceived(chatMessage: ChatMessage) {
        // TODO: store message in DB
        messages.add(chatMessage)
        view.notifyNewMessageAdded()
    }

    override fun start(messagingService: MessagingService) {
        messagingService.setCurrentParticipant(participantJid)
        messagingService.setOnMessageReceivedListener(this)
        this.messagingService = messagingService
    }

    override fun stop() {
        messagingService?.setOnMessageReceivedListener(null)
    }
}
