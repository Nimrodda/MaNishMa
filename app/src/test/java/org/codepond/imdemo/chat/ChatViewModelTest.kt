package org.codepond.imdemo.chat

import com.nhaarman.mockito_kotlin.*
import org.codepond.imdemo.ChatMessage
import org.codepond.imdemo.R
import org.codepond.imdemo.service.chat.MessagingService
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import org.junit.Assert.*
//import org.mockito.Mockito.*

class ChatViewModelTest {
    @Rule @JvmField
    var mException: ExpectedException  = ExpectedException.none()

    private lateinit var mMockService: MessagingService
    private lateinit var mChatViewModel: ChatViewModel

    @Before
    fun setUp() {
        mMockService = mock<MessagingService>()
        mChatViewModel = ChatViewModel("George", "John", mMockService)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun sendMessage_success() {
        mChatViewModel.messageText.set("Hello")
        mChatViewModel.clickSend()
        assertEquals("", mChatViewModel.messageText.get())
        verify(mMockService).sendMessage(any<ChatMessage>())
    }

    @Test
    fun sendMessage_withEmptyText_doesNothing() {
        mChatViewModel.clickSend()
        verify(mMockService, never()).sendMessage(any<ChatMessage>())
        assertEquals(0, mChatViewModel.messages.size.toLong())
    }

    @Test
    fun onMessageReceived() {
        val chatMessage = ChatMessage("ab", "ba")
        mChatViewModel.onMessageReceived(chatMessage)
        assertTrue(mChatViewModel.messages.size > 0)
    }

    @Test
    fun isAuthorVisible_incomingTrueSamePreviousAuthor_returnsFalse() {
        mChatViewModel.messages.add(MessageViewModel(ChatMessage("George", "Foo"), 0))
        val messageViewModel = MessageViewModel(ChatMessage("George", "Foo"), 1)
        mChatViewModel.messages.add(messageViewModel)
        assertFalse(mChatViewModel.isAuthorVisible(messageViewModel))
    }

    @Test
    fun isAuthorVisible_incomingTrueDifferentPreviousAuthor_returnsTrue() {
        mChatViewModel.messages.add(MessageViewModel(ChatMessage("George", "Foo"), 0))
        val messageViewModel = MessageViewModel(ChatMessage("John", "Foo"), 1)
        mChatViewModel.messages.add(messageViewModel)
        assertTrue(mChatViewModel.isAuthorVisible(messageViewModel))
    }

    @Test
    fun isAuthorVisible_localMessage_returnsFalse() {
        mChatViewModel.messages.add(MessageViewModel(ChatMessage("George", "Foo"), 0))
        val messageViewModel = MessageViewModel(ChatMessage("George", "Foo"), 1)
        mChatViewModel.messages.add(messageViewModel)
        assertFalse(mChatViewModel.isAuthorVisible(messageViewModel))
    }

    @Test
    fun isAuthorVisible_incomingTruePreviousMessageLocal_returnsTrue() {
        mChatViewModel.messages.add(MessageViewModel(ChatMessage("George", "Foo"), 0))
        val messageViewModel = MessageViewModel(ChatMessage("John", "Foo"), 1)
        mChatViewModel.messages.add(messageViewModel)
        assertTrue(mChatViewModel.isAuthorVisible(messageViewModel))
    }

    @Test
    fun getBackground_firstLocalMessage_returnsChatBubbleOutgoing() {
        val messageViewModel = MessageViewModel(ChatMessage("George", "Foo"), 0)
        mChatViewModel.messages.add(messageViewModel)
        assertEquals(R.drawable.chat_bubble_outgoing, mChatViewModel.getBackground(messageViewModel))
    }

    @Test
    fun getBackground_secondLocalMessage_returnsChatBubbleOutgoingExt() {
        mChatViewModel.messages.add(MessageViewModel(ChatMessage("George", "Foo"), 0))
        val messageViewModel = MessageViewModel(ChatMessage("George", "Foo"), 1)
        mChatViewModel.messages.add(messageViewModel)
        assertEquals(R.drawable.chat_bubble_outgoing_ext, mChatViewModel.getBackground(messageViewModel))
    }

    @Test
    fun getBackground_firstRemoteMessage_returnsChatBubbleIncoming() {
        val messageViewModel = MessageViewModel(ChatMessage("John", "Foo"), 0)
        mChatViewModel.messages.add(messageViewModel)
        assertEquals(R.drawable.chat_bubble_incoming, mChatViewModel.getBackground(messageViewModel))
    }

    @Test
    fun getBackground_secondRemoteMessage_returnsChatBubbleIncomingExt() {
        mChatViewModel.messages.add(MessageViewModel(ChatMessage("John", "Foo"), 0))
        val messageViewModel = MessageViewModel(ChatMessage("John", "Foo"), 1)
        mChatViewModel.messages.add(messageViewModel)
        assertEquals(R.drawable.chat_bubble_incoming_ext, mChatViewModel.getBackground(messageViewModel))
    }
}
