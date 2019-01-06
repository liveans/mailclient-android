package com.example.ahmet.securemailclient;

import android.app.Application;
import android.content.Context;

public class SecureClientApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        SecureClientApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return SecureClientApplication.context;
    }
}
