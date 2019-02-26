package com.myvnc.androidapplication.backgroundservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.FileObserver;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class TestService extends Service  {
    ObserbleFolder obserbleFolder;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("debug", "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("debug", "onStartCommand()");

        int requestCode = intent.getIntExtra("REQUEST_CODE",0);
        Context context = getApplicationContext();
        String channelId = "default";
        String title = context.getString(R.string.app_name);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Notification　Channel 設定
        NotificationChannel channel = new NotificationChannel(channelId, title , NotificationManager.IMPORTANCE_DEFAULT);
        if(notificationManager != null){
            notificationManager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(context, channelId)
                    .setContentTitle(title)
                    // android標準アイコンから
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setContentText("MediaPlay")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .build();
            // startForeground
            startForeground(1, notification);
        }
        obserbleFolder = new ObserbleFolder(getApplicationContext().getFilesDir().toString());
        obserbleFolder.startWatching();
        return START_NOT_STICKY;
        //return START_STICKY;
        //return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("debug", "onDestroy()");
        obserbleFolder.stopWatching();
        // Service終了
        stopSelf();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
