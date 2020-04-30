package com.example.mysympleapplication.hw6;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.mysympleapplication.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String SET_NOTIFICATION = "FirebaseNotification";
    private static final String TAG = "FirebaseTAG";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String notificationMessage = remoteMessage.getNotification().getBody();
        redirectToChatActivity(notificationMessage);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "NEW_TOKEN: " + token);      // отсюда токен брал для отправки именно моему телефону
    }

    private void redirectToChatActivity(String notificationn) {
        if (Main6Activity.isMainActivityRun) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(SET_NOTIFICATION, notificationn);
            startActivity(intent);
        }
        if (ChatActivity.isChatActivityRun) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(SET_NOTIFICATION, notificationn);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }

    }
}
