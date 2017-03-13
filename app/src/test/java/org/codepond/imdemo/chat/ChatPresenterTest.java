package org.codepond.imdemo.chat;

import org.codepond.imdemo.ChatMessage;
import org.codepond.imdemo.MessagingService;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ChatPresenterTest {
    @Rule public ExpectedException mException = ExpectedException.none();

    private MessagingService mMockService;
    private ChatContracts.View mMockView;
    private ChatPresenter mPresenter;

    @Before
    public void setUp() throws Exception {
        mMockService = mock(MessagingService.class);
        mMockView = mock(ChatContracts.View.class);
        mPresenter = new ChatPresenter(mMockView, "test@localhost", "test2@localhost");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void sendMessage_success() throws Exception {
        mPresenter.setMessagingService(mMockService);
        mPresenter.sendMessage("blablabla");
        verify(mMockView).notifyNewMessageAdded();
        verify(mMockView).cleanUserInput();
        verify(mMockService).sendMessage(any(ChatMessage.class));
        assertTrue(mPresenter.getMessages().size() > 0);
    }

    @Test
    public void sendMessage_serviceNotSet_throws() throws Exception {
        mException.expect(IllegalStateException.class);
        mPresenter.sendMessage("blablabla");
        verify(mMockView, never()).notifyNewMessageAdded();
        verify(mMockService, never()).sendMessage(any(ChatMessage.class));
        assertEquals(0, mPresenter.getMessages().size());
    }

    @Test
    public void sendMessage_withEmptyText_doesNothing() throws Exception {
        mPresenter.setMessagingService(mMockService);
        mPresenter.sendMessage(null);
        verify(mMockView, never()).notifyNewMessageAdded();
        verify(mMockService, never()).sendMessage(any(ChatMessage.class));
        assertEquals(0, mPresenter.getMessages().size());
    }

    @Test
    public void setMessagingService() throws Exception {
        mPresenter.setMessagingService(mMockService);
        verify(mMockService).setOnMessageReceivedListener(mPresenter);
    }

    @Test
    public void onMessageReceived() throws Exception {
        ChatMessage chatMessage = new ChatMessage("ab", "ba", "blabla", false, 12312312);
        mPresenter.onMessageReceived(chatMessage);
        verify(mMockView).notifyNewMessageAdded();
        assertTrue(mPresenter.getMessages().size() > 0);
    }
}