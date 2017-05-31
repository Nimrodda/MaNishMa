package org.codepond.imdemo.chat

import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import dagger.android.AndroidInjection

import org.codepond.imdemo.BR
import org.codepond.imdemo.R
import org.codepond.imdemo.databinding.ActivityChatBinding
import javax.inject.Inject

@BindingAdapter("chatViewModel", "messageViewModel")
fun remote(view: View, chatViewModel: ChatViewModel, messageViewModel: MessageViewModel) {
    val lp = view.layoutParams as RelativeLayout.LayoutParams
    if (chatViewModel.isIncoming(messageViewModel)) {
        lp.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
    } else {
        lp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
    }
}

class ChatActivity : AppCompatActivity() {
    @Inject lateinit var mChatViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityChatBinding>(this, R.layout.activity_chat)
        binding.model = mChatViewModel
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        layoutManager.stackFromEnd = true
        binding.messageList.layoutManager = layoutManager
        binding.messageList.itemAnimator = DefaultItemAnimator()
        binding.messageList.adapter = MessageAdapter(mChatViewModel.messages)
    }

    private inner class MessageAdapter
        constructor(private val mMessages: ObservableArrayList<MessageViewModel>) :
            RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

        private var mRecyclerView: RecyclerView? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.MessageViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return MessageViewHolder(DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false))
        }

        override fun onBindViewHolder(vh: MessageAdapter.MessageViewHolder, position: Int) {
            val messageViewModel = mMessages[position]
            vh.bind(messageViewModel, mChatViewModel)
        }

        override fun getItemViewType(position: Int): Int {
            if (mChatViewModel.isIncoming(mMessages[position])) {
                return R.layout.message_incoming
            } else {
                return R.layout.message_outgoing
            }
        }

        override fun getItemCount(): Int {
            return mMessages.size
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
            super.onAttachedToRecyclerView(recyclerView)
            mRecyclerView = recyclerView
            mMessages.addOnListChangedCallback(mOnListChangedCallback)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
            super.onDetachedFromRecyclerView(recyclerView)
            mMessages.removeOnListChangedCallback(mOnListChangedCallback)
        }


        private val mOnListChangedCallback = object: ObservableList.OnListChangedCallback<ObservableList<String>>() {
            override fun onItemRangeInserted(p0: ObservableList<String>?, p1: Int, p2: Int) {
                val newMessagePosition = mMessages.size
                notifyItemInserted(newMessagePosition)
                mRecyclerView?.smoothScrollToPosition(itemCount)

            }

            override fun onChanged(p0: ObservableList<String>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemRangeMoved(p0: ObservableList<String>?, p1: Int, p2: Int, p3: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemRangeChanged(p0: ObservableList<String>?, p1: Int, p2: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemRangeRemoved(p0: ObservableList<String>?, p1: Int, p2: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        inner class MessageViewHolder(var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bind(messageViewModel: MessageViewModel, chatViewModel: ChatViewModel) {
                binding.setVariable(BR.message, messageViewModel)
                binding.setVariable(BR.chat, chatViewModel)
                binding.setVariable(BR.context, applicationContext)
                binding.executePendingBindings()
            }
        }
    }

    companion object {
        val USER_ID = "extra_participant_jid"
        val CHAT_ID = "extra_chat_id"
    }
}
