package org.codepond.imdemo;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class ServiceModule {
    private Context mContext;

    public ServiceModule(Context context) {
        mContext = context;
    }

    @Provides MessagingServiceConnection provideMessagingService() {
        return new XmppMessagingService(mContext);
    }
}
