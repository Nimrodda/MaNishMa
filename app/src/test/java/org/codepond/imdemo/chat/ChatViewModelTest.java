package org.codepond.imdemo.chat;

import org.codepond.imdemo.ChatMessage;
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
        mChatViewModel = new ChatViewModel("test@localhost", "test2@localhost");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void sendMessage_success() throws Exception {
        mChatViewModel.start(mMockService);
        mChatViewModel.messageText.set("Hello");
        mChatViewModel.clickSend();
        assertEquals("", mChatViewModel.messageText.get());
        verify(mMockService).sendMessage(any(ChatMessage.class));
        assertTrue(mChatViewModel.getMessages().size() > 0);
    }

    @Test
    public void sendMessage_withEmptyText_doesNothing() throws Exception {
        mChatViewModel.start(mMockService);
        mChatViewModel.clickSend();
        verify(mMockService, never()).sendMessage(any(ChatMessage.class));
        assertEquals(0, mChatViewModel.getMessages().size());
    }

    @Test
    public void onMessageReceived() throws Exception {
        ChatMessage chatMessage = new ChatMessage("ab", "ba", "blabla", false, 12312312);
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
        mChatViewModel.start(null);
    }

    @Test
    public void stop_unregisterListener() throws Exception {
        mChatViewModel.start(mMockService);
        mChatViewModel.stop();
        verify(mMockService).setOnMessageReceivedListener(null);
    }
}
