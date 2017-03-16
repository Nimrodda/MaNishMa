package org.codepond.imdemo;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Context mContext;

    public AppModule(Context context) {
        mContext = context;
    }

    @Provides Context provideContext() {
        return mContext;
    }
}
