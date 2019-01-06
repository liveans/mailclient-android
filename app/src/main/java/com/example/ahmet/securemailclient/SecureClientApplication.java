package com.example.ahmet.securemailclient;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class SecureClientApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        SecureClientApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return SecureClientApplication.context;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
