package com.example.mysympleapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mysympleapplication.fragments.Fragment1;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        TextView textView = findViewById(R.id.text_description);
        String description = getIntent().getStringExtra(Fragment1.KEY_TITLE);
        if (description != null) {
            textView.setText(description);
        }
    }
}
