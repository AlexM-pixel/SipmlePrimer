package com.example.mysympleapplication.hw7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mysympleapplication.Prime_Activity;
import com.example.mysympleapplication.R;

public class Main7Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        Button buttonSearchFiles=findViewById(R.id.searchFile);
        Button buttonPhoroRedactor=findViewById(R.id.redactor);
        Button buttonTimer=findViewById(R.id.timer);
        buttonSearchFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(Main7Activity.this, SearcFilesActivity.class);
                startActivity(intent);
            }
        });
        buttonPhoroRedactor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main7Activity.this, PhotoRedactorActivity.class);
                startActivity(intent);
            }
        });
    }
}
