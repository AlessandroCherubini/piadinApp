package com.example.android.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.android.utility.Util;

public class MyStartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Util.scheduleJob(context);
    }
}