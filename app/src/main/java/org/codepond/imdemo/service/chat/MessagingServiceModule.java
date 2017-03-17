package org.codepond.imdemo.service.chat;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class MessagingServiceModule {
    private Context mContext;

    public MessagingServiceModule(Context context) {
        mContext = context;
    }

    @Provides MessagingServiceConnection provideMessagingService() {
        return new XmppMessagingService(mContext);
    }
}
