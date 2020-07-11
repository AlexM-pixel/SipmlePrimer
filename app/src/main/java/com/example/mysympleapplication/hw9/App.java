package com.example.mysympleapplication.hw9;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;



public class App extends Application {
    public static final String CHANNEL_1 = "channel_ID_1";
    public static final String CHANNEL_2 = "channel_ID_2";
    public static Context context = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel1 = new NotificationChannel(CHANNEL_1, "channel1", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel1.setDescription("This is Channel 1");
            notificationChannel1.enableLights(true);
            notificationChannel1.setLightColor(Color.RED);
            notificationChannel1.enableVibration(true);
            NotificationChannel notificationChannel2 = new NotificationChannel(CHANNEL_2, "channel2", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel2.setDescription("This is Channel 2");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel1);
            notificationManager.createNotificationChannel(notificationChannel2);
        }
    }
}
