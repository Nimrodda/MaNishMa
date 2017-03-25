package org.codepond.imdemo.chat

import javax.inject.Named

import dagger.Module
import dagger.Provides

@Module
class ChatModule(
        private val participantJid: String,
        private val userJid: String,
        private val view: ChatContracts.View) {

    @Provides fun provideView(): ChatContracts.View {
        return view
    }

    @Provides @Named("participantJid") fun provideParticipantJid(): String {
        return participantJid
    }

    @Provides @Named("userJid") fun provideUserJid(): String {
        return userJid
    }
}
