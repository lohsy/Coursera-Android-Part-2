package com.mikelohsy.dailyselfie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    final String TAG = "DAILY_SELFIE";

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received intent");
        Intent notificationServiceIntent = new Intent(context, NotificationService.class);
        context.startService(notificationServiceIntent);
    }
}
