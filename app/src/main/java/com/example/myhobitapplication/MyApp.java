package com.example.myhobitapplication;

import android.app.Application;
import com.onesignal.OneSignal;

public class MyApp extends Application {
    private static final String ONESIGNAL_APP_ID = "d6359313-ac1a-496b-be12-07cd18dcfa46";
    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);


    }
}
