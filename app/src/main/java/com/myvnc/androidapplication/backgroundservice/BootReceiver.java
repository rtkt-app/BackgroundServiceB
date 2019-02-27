package com.myvnc.androidapplication.backgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceStart = new Intent(context, TestService.class);
            serviceStart.putExtra("REQUEST_CODE", 1);
            context.startForegroundService(serviceStart);
        }
    }
}
