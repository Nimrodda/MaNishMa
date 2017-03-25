package org.codepond.imdemo.chat

import org.codepond.imdemo.BasePresenter
import org.codepond.imdemo.ChatMessage

interface ChatContracts {
    interface Presenter : BasePresenter {
        fun sendMessage(message: String)
        fun loadMessages()
    }

    interface View {
        fun showMessages(chatMessages: MutableList<ChatMessage>)
        fun notifyNewMessageAdded()
        fun cleanUserInput()
    }
}
