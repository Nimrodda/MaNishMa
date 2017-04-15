package org.codepond.imdemo.chat;

import org.codepond.imdemo.ChatMessage;
import org.codepond.imdemo.R;
import org.codepond.imdemo.service.chat.MessagingService;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ChatViewModelTest {
    @Rule public ExpectedException mException = ExpectedException.none();

    private MessagingService mMockService;
    private ChatViewModel mChatViewModel;

    @Before
    public void setUp() throws Exception {
        mMockService = mock(MessagingService.class);
        mChatViewModel = new ChatViewModel("test@localhost", "test2@localhost", mMockService);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void sendMessage_success() throws Exception {
        mChatViewModel.messageText.set("Hello");
        mChatViewModel.clickSend();
        assertEquals("", mChatViewModel.messageText.get());
        verify(mMockService).sendMessage(any(ChatMessage.class));
        assertTrue(mChatViewModel.getMessages().size() > 0);
    }

    @Test
    public void sendMessage_withEmptyText_doesNothing() throws Exception {
        mChatViewModel.clickSend();
        verify(mMockService, never()).sendMessage(any(ChatMessage.class));
        assertEquals(0, mChatViewModel.getMessages().size());
    }

    @Test
    public void onMessageReceived() throws Exception {
        ChatMessage chatMessage = new ChatMessage("ab", "ba", null);
        mChatViewModel.onMessageReceived(chatMessage);
        assertTrue(mChatViewModel.getMessages().size() > 0);
    }

    @Test
    public void loadMessages() throws Exception {
        mChatViewModel.loadMessages();
        // TODO: refactor this test
    }

    @Test
    public void start_nullService_throws() throws Exception {
        mException.expect(NullPointerException.class);
    }

    @Test
    public void stop_unregisterListener() throws Exception {
        verify(mMockService).setOnMessageReceivedListener(null);
    }

    @Test
    public void isAuthorVisible_incomingTrueSamePreviousAuthor_returnsFalse() throws Exception {
        mChatViewModel.getMessages().add(new MessageViewModel(new ChatMessage("George", "Foo", null), 0));
        MessageViewModel messageViewModel = new MessageViewModel(new ChatMessage("George", "Foo", null), 1);
        mChatViewModel.getMessages().add(messageViewModel);
        assertFalse(mChatViewModel.isAuthorVisible(messageViewModel));
    }

    @Test
    public void isAuthorVisible_incomingTrueDifferentPreviousAuthor_returnsTrue() throws Exception {
        mChatViewModel.getMessages().add(new MessageViewModel(new ChatMessage("George", "Foo", null), 0));
        MessageViewModel messageViewModel = new MessageViewModel(new ChatMessage("John", "Foo", null), 1);
        mChatViewModel.getMessages().add(messageViewModel);
        assertTrue(mChatViewModel.isAuthorVisible(messageViewModel));
    }

    @Test
    public void isAuthorVisible_localMessage_returnsFalse() throws Exception {
        mChatViewModel.getMessages().add(new MessageViewModel(new ChatMessage("George", "Foo", null), 0));
        MessageViewModel messageViewModel = new MessageViewModel(new ChatMessage("John", "Foo", null), 1);
        mChatViewModel.getMessages().add(messageViewModel);
        assertFalse(mChatViewModel.isAuthorVisible(messageViewModel));
    }

    @Test
    public void isAuthorVisible_incomingTruePreviousMessageLocal_returnsTrue() throws Exception {
        mChatViewModel.getMessages().add(new MessageViewModel(new ChatMessage("George", "Foo", null), 0));
        MessageViewModel messageViewModel = new MessageViewModel(new ChatMessage("John", "Foo", null), 1);
        mChatViewModel.getMessages().add(messageViewModel);
        assertTrue(mChatViewModel.isAuthorVisible(messageViewModel));
    }

    @Test
    public void getBackground_firstLocalMessage_returnsChatBubbleOutgoing() throws Exception {
        MessageViewModel messageViewModel = new MessageViewModel(new ChatMessage("John", "Foo", null), 0);
        mChatViewModel.getMessages().add(messageViewModel);
        assertEquals(R.drawable.chat_bubble_outgoing, mChatViewModel.getBackground(messageViewModel));
    }

    @Test
    public void getBackground_secondLocalMessage_returnsChatBubbleOutgoingExt() throws Exception {
        mChatViewModel.getMessages().add(new MessageViewModel(new ChatMessage("John", "Foo", null), 0));
        MessageViewModel messageViewModel = new MessageViewModel(new ChatMessage("John", "Foo", null), 1);
        mChatViewModel.getMessages().add(messageViewModel);
        assertEquals(R.drawable.chat_bubble_outgoing_ext, mChatViewModel.getBackground(messageViewModel));
    }

    @Test
    public void getBackground_firstRemoteMessage_returnsChatBubbleIncoming() throws Exception {
        MessageViewModel messageViewModel = new MessageViewModel(new ChatMessage("John", "Foo", null), 0);
        mChatViewModel.getMessages().add(messageViewModel);
        assertEquals(R.drawable.chat_bubble_incoming, mChatViewModel.getBackground(messageViewModel));
    }

    @Test
    public void getBackground_secondRemoteMessage_returnsChatBubbleIncomingExt() throws Exception {
        mChatViewModel.getMessages().add(new MessageViewModel(new ChatMessage("John", "Foo", null), 0));
        MessageViewModel messageViewModel = new MessageViewModel(new ChatMessage("John", "Foo", null), 1);
        mChatViewModel.getMessages().add(messageViewModel);
        assertEquals(R.drawable.chat_bubble_incoming_ext, mChatViewModel.getBackground(messageViewModel));
    }
}
