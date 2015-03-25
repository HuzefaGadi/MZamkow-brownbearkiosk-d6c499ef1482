package com.huzefagadi.brownbear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PhotoReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
        }*/
        Intent intentToSend = new Intent("com.huzefagadi.brownbear.NOTIFY");
        context.sendBroadcast(intentToSend);
    }
}