package org.codepond.imdemo;

import android.app.Application;

public class App extends Application {
    private ServiceComponent mServiceComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mServiceComponent = DaggerServiceComponent.builder()
                .appModule(new AppModule(getApplicationContext()))
                .build();
    }

    public ServiceComponent getServiceComponent() {
        return mServiceComponent;
    }
}
