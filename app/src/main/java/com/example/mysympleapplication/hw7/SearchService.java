package com.example.mysympleapplication.hw7;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.mysympleapplication.R;

import java.io.File;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

public class SearchService extends IntentService {
    String nameFile;
    int count;
    Notification notification;
    NotificationManager notificationManager;
    NotificationCompat.Builder notificationCompat;
    private static int NOTIFY_ID = 15;
    private static int FOREGROUND_ID = 18;
    private static final String CHANNEL_ID = "channel_id";
    public static final String SEND_COUNT_FOR_ACTIVITY = "countFiles";
    public static final String COUNT_FILES = "count_files";
    private String tag = "tag_for_searchFiles";

    public SearchService() {
        super("name_thread");
        Log.d(tag, "constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(tag, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(tag, "onStartCommand");
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(tag, "onHandleIntent");
        String pathName = intent.getStringExtra(SearcFilesActivity.LIST_FILES);
        nameFile = intent.getStringExtra(SearcFilesActivity.NAME_FILE);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(FOREGROUND_ID, buildForegroundNotification(nameFile));
        File f = new File(pathName);
        searchfilesMethod(f);
        Intent intent1 = new Intent(SEND_COUNT_FOR_ACTIVITY);
        intent1.putExtra(COUNT_FILES, count);
        sendBroadcast(intent1);
    }

    private void searchfilesMethod(File f) {
        File[] list = f.listFiles();
        if (list != null) {
            for (File file : list) {
                if (file.isDirectory()) {
                    searchfilesMethod(file);
                } else if (file.getName().contains("." + nameFile)) {
                    count++;
                    notificationCompat.setContentText(count + " files found");
                    notification = notificationCompat.build();
                    notificationManager.notify(NOTIFY_ID, notification);
                }
            }
        }
    }


    private Notification buildForegroundNotification(String files_name) {
        Intent intent = new Intent(this, SearcFilesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationCompat = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Search " + files_name)
                .setContentText(count + " files found")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(PRIORITY_HIGH);
        createChannellIfNeeded(notificationManager);
        notification = notificationCompat.build();
        notificationManager.notify(NOTIFY_ID, notification);  // отображает уведомление
        return notification;
    }

    private void createChannellIfNeeded(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


}