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

import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ChatModule {
    @Provides @Named("userId") fun provideUserId(chatActivity: ChatActivity): String = chatActivity.intent.getStringExtra(ChatActivity.USER_ID)
    @Provides @Named("chatId") fun provideChatId(chatActivity: ChatActivity): String = chatActivity.intent.getStringExtra(ChatActivity.CHAT_ID)
}
