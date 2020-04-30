package com.example.mysympleapplication.hw6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysympleapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class ChatActivity extends AppCompatActivity {
    public static boolean isChatActivityRun;
    private  String notif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            notif = arguments.getString(MyFirebaseMessagingService.SET_NOTIFICATION);
        }
            TextView textView = findViewById(R.id.textView_forChat);
            notif = textView.getText().toString() + "\n" + notif;
            textView.setText(notif);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isChatActivityRun = true;
    }

    public  void setNotif(String notification) {
        this.notif = notification;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isChatActivityRun= false;
    }
}
