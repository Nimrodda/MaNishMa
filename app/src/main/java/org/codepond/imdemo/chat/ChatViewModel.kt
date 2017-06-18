package org.codepond.imdemo.chat

import android.databinding.ObservableArrayList
import android.databinding.ObservableField

import org.codepond.imdemo.ChatMessage
import org.codepond.imdemo.R
import org.codepond.imdemo.service.chat.MessagingService
import javax.inject.Inject
import javax.inject.Named

class ChatViewModel
    @Inject constructor(@Named("userId") private val mUserId: String,
                        @Named("chatId") private val mChatId: String,
                        private val mMessagingService: MessagingService) : MessagingService.OnMessageReceivedListener {

    var messageText = ObservableField<String>()
    val messages = ObservableArrayList<MessageViewModel>()

    init {
        mMessagingService.setOnMessageReceivedListener(this)
        messageText.set("")
    }

    fun clickSend() {
        if (messageText.get() != null && messageText.get().isNotEmpty()) {
            val chatMessage = ChatMessage(mUserId, messageText.get())
            messages.add(MessageViewModel(chatMessage, messages.size))
            messageText.set("")
            mMessagingService.sendMessage(chatMessage)
        }
    }

    fun isAuthorVisible(model: MessageViewModel): Boolean {
        return isIncoming(model) && !isPreviousAuthorSame(model)
    }

    fun getBackground(model: MessageViewModel): Int {
        if (isIncoming(model)) {
            if (isPreviousAuthorSame(model)) {
                return R.drawable.chat_bubble_incoming_ext
            } else {
                return R.drawable.chat_bubble_incoming
            }
        } else {
            if (isPreviousAuthorSame(model)) {
                return R.drawable.chat_bubble_outgoing_ext
            } else {
                return R.drawable.chat_bubble_outgoing
            }
        }
    }

    private fun isPreviousAuthorSame(model: MessageViewModel): Boolean {
        val position = model.position
        return position > 0 && messages[position - 1].author == model.author
    }

    fun isIncoming(model: MessageViewModel): Boolean {
        return mUserId != model.author
    }

    override fun onMessageReceived(chatMessage: ChatMessage) {
        if (chatMessage.from != mUserId) {
            messages.add(MessageViewModel(chatMessage, messages.size))
        }
    }
}
