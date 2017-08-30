package org.codepond.imdemo.chat

import android.support.test.rule.ActivityTestRule
import android.support.test.rule.UiThreadTestRule
import android.support.test.runner.AndroidJUnit4
import org.codepond.imdemo.model.ChatMessage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatActivityTest {
    @Rule @JvmField
    val mActivityTestRule: ActivityTestRule<ChatActivity> = ActivityTestRule<ChatActivity>(ChatActivity::class.java, false, false)

    @Rule @JvmField
    val mUiThreadTestRule = UiThreadTestRule()

    @Test
    fun typeMessageAndClickSend_messageAddedToList() {
        mActivityTestRule.launchActivity(null)
        ChatRobot()
                .typeMessage("Hello")
                .clickSend()
                .isShownOnRightSide()
    }

    @Test
    fun receivedRemoteMessage_addedToList() {
        val messageText = "Hello"
        val chatMessage = ChatMessage("Joni", messageText)
        mActivityTestRule.launchActivity(null)
        mUiThreadTestRule.runOnUiThread { mActivityTestRule.activity.mChatViewModel.onMessageReceived(chatMessage) }

        ChatRobot()
                .receiveMessage(messageText)
                .isShownOnLeftSide()
    }
}
