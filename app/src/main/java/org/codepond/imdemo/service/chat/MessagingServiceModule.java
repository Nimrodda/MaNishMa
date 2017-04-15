package org.codepond.imdemo.service.chat;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class MessagingServiceModule {
    @Binds public abstract MessagingService provideMessagingService(FirebaseMessagingService firebaseMessagingService);
}
