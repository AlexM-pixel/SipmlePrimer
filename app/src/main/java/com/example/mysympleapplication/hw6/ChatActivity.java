package com.example.mysympleapplication.hw6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mysympleapplication.R;

public class ChatActivity extends AppCompatActivity {
    public static boolean isChatActivityRun;
    private String notification;
    TextView textView;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        textView = findViewById(R.id.textView_forChat);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            notification = arguments.getString(MyFirebaseMessagingService.SET_NOTIFICATION);
            textView.setText(notification);
        }
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notification = intent.getStringExtra(MyFirebaseMessagingService.SET_NOTIFICATION);
                notification = textView.getText().toString() + "\n" + notification;
                textView.setText(notification);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        isChatActivityRun = true;
        IntentFilter filter = new IntentFilter(MyFirebaseMessagingService.INTENT_SEND_MESSAGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,filter );

    }

    @Override
    protected void onPause() {
        super.onPause();
        isChatActivityRun = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}
