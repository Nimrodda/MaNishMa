package org.codepond.imdemo.chat;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
class ChatModule {
    private final String mParticipantJid;
    private final String mUserJid;
    private final ChatContracts.View mView;

    ChatModule(String participantJid, String userJid, ChatContracts.View view) {
        mParticipantJid = participantJid;
        mUserJid = userJid;
        mView = view;
    }

    @Provides ChatContracts.View provideView() {
        return mView;
    }

    @Provides @Named("participantJid") String provideParticipantJid() {
        return mParticipantJid;
    }

    @Provides @Named("userJid") String provideUserJid() {
        return mUserJid;
    }
}
