package org.codepond.imdemo.chat

import android.content.Intent
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.UiThreadTestRule
import android.support.test.runner.AndroidJUnit4
import org.codepond.imdemo.ChatMessage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatActivityTest {
    @Rule @JvmField
    val mActivityTestRule: ActivityTestRule<ChatActivity> = object : ActivityTestRule<ChatActivity>(ChatActivity::class.java) {
        override fun getActivityIntent(): Intent {
            val intent = Intent()
            intent.putExtra(ChatActivity.EXTRA_PARTICIPANT_JID, "user1@localhost/rLMACndayan")
            return intent
        }
    }

    @Rule @JvmField
    val mUiThreadTestRule = UiThreadTestRule()

    @Test
    fun typeMessageAndClickSend_messageAddedToList() {
        ChatRobot()
                .typeMessage("Hello")
                .clickSend()
                .isShownOnRightSide()


    }

    @Test
    fun receivedRemoteMessage_addedToList() {
        val messageText = "Hello"
        val chatMessage = ChatMessage("user2@localhost/rLMACndayan", "user1@localhost/rLMACndayan", messageText, true, System.currentTimeMillis())
        val activity = mActivityTestRule.activity

        mUiThreadTestRule.runOnUiThread { activity.presenter.onMessageReceived(chatMessage) }

        ChatRobot()
                .receiveMessage(messageText)
                .isShownOnLeftSide()
    }
}