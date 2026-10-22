package com.bumbumapps.bouncy;

import android.app.Application;

import com.bumbumapps.bouncy.util.EdgeToEdgeCallbacks;

public class PinballApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EdgeToEdgeCallbacks.register(this);
    }
}
