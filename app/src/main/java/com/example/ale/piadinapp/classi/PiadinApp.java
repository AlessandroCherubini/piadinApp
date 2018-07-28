package com.example.ale.piadinapp.classi;

import android.app.Application;
import android.content.Context;

public class PiadinApp extends Application {
    private static Context context;
    public void onCreate() {
        super.onCreate();
        PiadinApp.context = getApplicationContext();
    }
    public static Context getAppContext() {
        return PiadinApp.context;
    }
}
