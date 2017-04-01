package org.codepond.imdemo.service.chat;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent(modules = MessagingServiceModule.class)
public interface MessagingServiceSubComponent extends AndroidInjector<ChatService> {
    @Subcomponent.Builder
    public abstract class Builder extends AndroidInjector.Builder<ChatService> {}
}
