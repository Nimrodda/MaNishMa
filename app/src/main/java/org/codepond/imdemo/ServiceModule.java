package org.codepond.imdemo;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ServiceModule {

    @Singleton
    @Binds
    abstract MessagingService provideMessagingService(XmppMessagingService xmppMessagingService);
}
