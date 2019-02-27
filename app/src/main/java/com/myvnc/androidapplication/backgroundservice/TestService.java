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
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentText("Secure Camera is running …")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .build();
            // startForeground
            startForeground(1, notification);
        }
        obserbleFolder = new ObserbleFolder(getApplicationContext().getFilesDir().toString(), getApplication()){
            // ディレクトリ内で、変化が起きたら呼ばれるイベント
            @Override
            public void onEvent(int event, String path) {
                if(path!=null){
                    Log.d("Observer Event", path);
                    switch (event) {
                        case FileObserver.ACCESS:
                            Log.d("Observer Event", "ACCESS");
                            break;
                        case FileObserver.ALL_EVENTS:
                            Log.d("Observer Event", "ALL_EVENTS");
                            break;
                        case FileObserver.ATTRIB:
                            Log.d("Observer Event", "ATTRIB");
                            break;
                        case FileObserver.CLOSE_NOWRITE:
                            Log.d("Observer Event", "NOWRITE");
                            break;
                        case FileObserver.CLOSE_WRITE:
                            Log.d("Observer Event", "WRITE");
                            SendFileTask sendFileTask = new SendFileTask(application);
                            sendFileTask.execute(application.getApplicationContext().getFilesDir()+"/"+path);
                            break;
                        case FileObserver.CREATE:
                            Log.d("Observer Event", "CREATE");
                            break;
                        case FileObserver.DELETE:
                            Log.d("Observer Event", "DELETE");
                            break;
                        case FileObserver.DELETE_SELF:
                            Log.d("Observer Event", "DELETE_SELF");
                            break;
                        case FileObserver.MODIFY:
                            Log.d("Observer Event", "MODIFY");
                            break;
                        case FileObserver.MOVED_FROM:
                            Log.d("Observer Event", "MOVED_FROM");
                            break;
                        case FileObserver.MOVED_TO:
                            Log.d("Observer Event", "MOVED_TO");
                            break;
                        case FileObserver.MOVE_SELF:
                            Log.d("Observer Event", "MOVE_SELF");
                            break;
                        case FileObserver.OPEN:
                            Log.d("Observer Event", "OPEN");
                            break;
                    }
                }
            }
        };
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
