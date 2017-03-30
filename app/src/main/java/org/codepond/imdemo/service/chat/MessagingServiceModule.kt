package org.codepond.imdemo.service.chat

import android.content.Context

import dagger.Module
import dagger.Provides

@Module
class MessagingServiceModule(private val mContext: Context) {

    @Provides internal fun provideMessagingService(): MessagingServiceConnection {
        return XmppMessagingService(mContext)
    }
}
