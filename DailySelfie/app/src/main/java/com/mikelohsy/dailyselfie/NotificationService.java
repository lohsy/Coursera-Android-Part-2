package com.mikelohsy.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NotificationService extends Service {
    final String TAG = "DAILY_SELFIE";

    private static final int ID = 1;
    private static final String TICKERTEXT = "Take a selfie now!";
    private static final String TITLETEXT = "Daily Selfie Reminder";
    private static final String CONTENTTEXT = "Take a selfie!!!";

    private NotificationManager mNotificationManager;

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i(TAG, "Starting service");
        mNotificationManager = (NotificationManager)
                getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        Intent newIntent = new Intent(this, MainActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this).setTicker(TICKERTEXT)
                .setSmallIcon(R.drawable.ic_action_camera_small).setAutoCancel(true)
                .setContentTitle(TITLETEXT).setContentText(CONTENTTEXT)
                .setContentIntent(pendingIntent);

        mNotificationManager.notify(ID, builder.build());

        return START_STICKY;
    }
}