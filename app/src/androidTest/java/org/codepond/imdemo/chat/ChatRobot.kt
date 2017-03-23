/*
 * Copyright 2017 Nimrod Dayan CodePond.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codepond.imdemo.chat

import android.support.test.espresso.Espresso.*
import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.RelativeLayout
import org.codepond.imdemo.R
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.Assert.assertEquals


fun isListItem(): Matcher<View> {
    return isDescendantOfA(isAssignableFrom(RecyclerView::class.java))
}

fun isAlignParent(alignParent: Int): ViewAssertion {
    return ViewAssertion { view, noViewFoundException ->
        if (noViewFoundException != null) {
            throw noViewFoundException
        }
        assertThat("View must be a child of RelativeLayout", view.parent, instanceOf<Any>(RelativeLayout::class.java))
        val params = view.layoutParams as RelativeLayout.LayoutParams
        val rules = params.rules
        assertEquals(RelativeLayout.TRUE, rules[alignParent])
    }
}

class ChatRobot {
    inner class Action {
        fun clickSend(): Result {
            onView(withId(R.id.button_send)).perform(click())
            return Result()
        }
    }
    inner class Result {
        fun isShownOnRightSide() {
            return isShown(RelativeLayout.ALIGN_PARENT_RIGHT)
        }

        fun isShownOnLeftSide() {
            return isShown(RelativeLayout.ALIGN_PARENT_LEFT)
        }

        private fun isShown(alignParent: Int) {
            onView(withId(R.id.message_text)).check(matches(withText("")))
            onView(withId(R.id.message_item)).check(matches(allOf<View>(isListItem(), withText(text), isDisplayed())))
            onView(withId(R.id.message_container)).check(isAlignParent(alignParent))
        }
    }

    private lateinit var text: String

    fun receiveMessage(text: String): Result {
        this.text = text
        return Result()
    }

    fun typeMessage(text: String): Action {
        this.text = text
        onView(withId(R.id.message_text)).perform(typeText(text))
        return Action()
    }
}
