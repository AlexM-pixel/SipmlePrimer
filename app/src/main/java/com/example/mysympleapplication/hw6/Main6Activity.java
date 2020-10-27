package com.example.mysympleapplication.hw6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mysympleapplication.R;

public class Main6Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);
        Button buttonToChat = findViewById(R.id.buttonToChat);
        buttonToChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main6Activity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

    }
}
