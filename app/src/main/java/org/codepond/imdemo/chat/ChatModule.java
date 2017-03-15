package org.codepond.imdemo.chat;

import dagger.Module;
import dagger.Provides;

@Module
class ChatModule {
    private String mParticipantJid;
    private ChatContracts.View mView;

    ChatModule(String participantJid, ChatContracts.View view) {
        this.mParticipantJid = participantJid;
        this.mView = view;
    }

    @Provides ChatContracts.Presenter providePresenter() {
        return new ChatPresenter(mView, "test@localhost", mParticipantJid);
    }
}
