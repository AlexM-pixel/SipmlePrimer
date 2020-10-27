package com.example.mysympleapplication.hw6;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String SET_NOTIFICATION = "FirebaseNotification";
    private static final String TAG = "FirebaseTAG";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String notificationMessage = remoteMessage.getNotification().getBody();
        setIntentSendMessage(notificationMessage);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "NEW_TOKEN: " + token);      // отсюда токен брал для отправки именно моему телефону
    }

    private void setIntentSendMessage(String notification) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(SET_NOTIFICATION, notification);
        startActivity(intent);
    }
}
