package org.codepond.imdemo.chat

import android.content.ComponentName
import android.os.Bundle
import android.os.IBinder
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView

import org.codepond.imdemo.BaseActivity
import org.codepond.imdemo.ChatMessage
import org.codepond.imdemo.service.chat.ChatService
import org.codepond.imdemo.R

import java.text.SimpleDateFormat

import javax.inject.Inject

class ChatActivity : BaseActivity(), ChatContracts.View {
    companion object {
        @JvmStatic
        val EXTRA_PARTICIPANT_JID = "extra_participant_jid"
    }
    private var mAdapter: MessageAdapter? = null
    private var mMessageText: EditText? = null
    private var mRecyclerView: RecyclerView? = null
    @Inject lateinit var presenter: ChatPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val participantJid = intent.getStringExtra(EXTRA_PARTICIPANT_JID)
        val userJid = "test@localhost"

        DaggerChatComponent.builder()
                .chatModule(ChatModule(participantJid, userJid, this)).build()
                .inject(this)
        setContentView(R.layout.activity_chat)
        val verticalLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        verticalLayoutManager.stackFromEnd = true
        mAdapter = MessageAdapter()
        mRecyclerView = findViewById(R.id.message_list) as RecyclerView
        mRecyclerView?.let {
            it.adapter = mAdapter
            it.layoutManager = verticalLayoutManager
            it.itemAnimator = DefaultItemAnimator()
        }
        mMessageText = findViewById(R.id.message_text) as EditText
        findViewById(R.id.button_send).setOnClickListener {
            presenter.sendMessage(mMessageText?.text.toString())
        }
        presenter.loadMessages()
    }

    override fun showMessages(chatMessages: MutableList<ChatMessage>) {
        mAdapter?.setMessages(chatMessages)
    }

    override fun notifyNewMessageAdded() {
        mAdapter?.notifyNewMessageAdded()
        mRecyclerView?.smoothScrollToPosition(mAdapter!!.itemCount)
    }

    override fun cleanUserInput() {
        mMessageText?.setText("")
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        val messagingService = (service as ChatService.LocalBinder).service
        presenter.start(messagingService)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        presenter.stop()
    }

    private inner class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
        private var mMessages: MutableList<ChatMessage>? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.MessageViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return MessageViewHolder(inflater.inflate(R.layout.message_item, parent, false))
        }

        override fun onBindViewHolder(vh: MessageAdapter.MessageViewHolder, position: Int) {
            val lp = vh.container.layoutParams as RelativeLayout.LayoutParams
            val chatMessage = mMessages?.get(position)
            if (chatMessage != null) {
                if (chatMessage.incomingMessage) {
                    if (isPreviousAuthorSame(position, chatMessage.from)) {
                        vh.container.setBackgroundResource(R.drawable.chat_bubble_incoming_ext)
                    } else {
                        vh.author.text = chatMessage.from
                        vh.author.visibility = android.view.View.VISIBLE
                        vh.container.setBackgroundResource(R.drawable.chat_bubble_incoming)

                    }
                    lp.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
                } else {
                    if (isPreviousAuthorSame(position, chatMessage.from)) {
                        vh.container.setBackgroundResource(R.drawable.chat_bubble_outgoing_ext)
                    } else {
                        vh.container.setBackgroundResource(R.drawable.chat_bubble_outgoing)
                    }
                    lp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
                }
                vh.container.layoutParams = lp
                vh.messageItem.text = chatMessage.messageText
                vh.timestamp.text = SimpleDateFormat.getInstance().format(chatMessage.timestamp)
            }
        }

        private fun isPreviousAuthorSame(position: Int, displayName: String): Boolean {
            return position > 0 && mMessages?.get(position - 1)?.from == displayName
        }

        override fun getItemCount(): Int {
            return mMessages?.size ?: 0
        }

        internal fun notifyNewMessageAdded() {
            val newMessagePosition = mMessages?.size ?: 0
            notifyItemInserted(newMessagePosition)
        }

        internal fun setMessages(chatMessages: MutableList<ChatMessage>) {
            mMessages = chatMessages
            notifyDataSetChanged()
        }

        internal inner class MessageViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
            var messageItem: TextView = view.findViewById(R.id.message_item) as TextView
            var timestamp: TextView = view.findViewById(R.id.timestamp) as TextView
            var author: TextView = view.findViewById(R.id.author) as TextView
            var container: android.view.View = view.findViewById(R.id.message_container)
        }
    }
}
