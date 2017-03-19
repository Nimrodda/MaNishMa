package org.codepond.imdemo.chat;

import android.content.Intent;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.assertion.PositionAssertions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.UiThreadTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import org.codepond.imdemo.ChatMessage;
import org.codepond.imdemo.R;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ChatActivityTest {
    @Rule
    public ActivityTestRule<ChatActivity> mActivityTestRule = new ActivityTestRule<ChatActivity>(ChatActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent();
            intent.putExtra(ChatActivity.EXTRA_PARTICIPANT_JID, "user1@localhost/rLMACndayan");
            return intent;
        }
    };

    @Rule
    public UiThreadTestRule mUiThreadTestRule = new UiThreadTestRule();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void typeMessageAndClickSend_messageAddedToList() throws Exception {
        String messageText = "Hello";

        // type message
        onView(withId(R.id.message_text)).perform(replaceText(messageText), closeSoftKeyboard());

        // Click send
        onView(withId(R.id.button_send)).perform(click());

        // Check that EditText is cleared
        onView(withId(R.id.message_text)).check(matches(withText("")));

        // Check that the message was added to the list
        onView(withId(R.id.message_item)).check(matches(allOf(isListItem(), withText(messageText), isDisplayed())));

        // Assert message is on the right side
        onView(withId(R.id.message_container)).check(isAlignParentRight());
    }

    @Test
    public void receivedRemoteMessage_addedToList() throws Throwable {
        String messageText = "Hello";
        final ChatMessage chatMessage = new ChatMessage("user2@localhost/rLMACndayan", "user1@localhost/rLMACndayan", messageText, true, System.currentTimeMillis());
        final ChatActivity activity = mActivityTestRule.getActivity();

        mUiThreadTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.mPresenter.onMessageReceived(chatMessage);
            }
        });

        // Check that the message was added to the list
        onView(withId(R.id.message_item)).check(matches(allOf(isListItem(), withText(messageText), isDisplayed())));

        // Assert message is on left side
        onView(withId(R.id.message_container)).check(isAlignParentLeft());
    }

    private static Matcher<View> isListItem() {
        return isDescendantOfA(isAssignableFrom(RecyclerView.class));
    }

    private static ViewAssertion isAlignParentRight() {
        return isAlignParent(RelativeLayout.ALIGN_PARENT_RIGHT);
    }

    private static ViewAssertion isAlignParentLeft() {
        return isAlignParent(RelativeLayout.ALIGN_PARENT_LEFT);
    }

    private static ViewAssertion isAlignParent(final int alignParent) {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null) {
                    throw noViewFoundException;
                }
                assertThat("View must be a child of RelativeLayout", view.getParent(), instanceOf(RelativeLayout.class));
                RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) view.getLayoutParams());
                int[] rules = params.getRules();
                assertEquals(RelativeLayout.TRUE, rules[alignParent]);
            }
        };
    }
}