package com.example.mysympleapplication.hw3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mysympleapplication.R;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
    }

    public void showingConstraintActivity(View v) {
        Intent intent = new Intent(this, ConstrainActivity.class);
        startActivity(intent);
    }

}


