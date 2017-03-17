package org.codepond.imdemo.service.chat;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
class MessagingServiceModule {
    private Context mContext;

    MessagingServiceModule(Context context) {
        mContext = context;
    }

    @Provides MessagingServiceConnection MessagingServiceConnection() {
        return new FakeMessagingService();
    }
}
