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
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        textView = findViewById(R.id.textView_forChat);
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            String notification = arguments.getString(MyFirebaseMessagingService.SET_NOTIFICATION);
            textView.setText(notification);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String message = intent.getStringExtra(MyFirebaseMessagingService.SET_NOTIFICATION);
        message = textView.getText().toString() + "\n" + message;
        textView.setText(message);
    }

}
