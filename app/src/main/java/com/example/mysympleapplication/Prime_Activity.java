package com.example.mysympleapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mysympleapplication.hw1.Main1Activity;
import com.example.mysympleapplication.hw2.Main2Activity;
import com.example.mysympleapplication.hw3.Main3Activity;
import com.example.mysympleapplication.hw4.Main4Activity;
import com.example.mysympleapplication.hw5.Main5Activity;
import com.example.mysympleapplication.hw6.Main6Activity;
import com.example.mysympleapplication.hw7.Main7Activity;
import com.example.mysympleapplication.hw8.Main8Activity;
import com.example.mysympleapplication.hw9.Main9Activity;

public class Prime_Activity extends AppCompatActivity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prime);
        Button homeWork1 = findViewById(R.id.button1HW);
        Button homeWork2 = findViewById(R.id.button2HW);
        Button homeWork3 = findViewById(R.id.button3HW);
        Button homeWork4 = findViewById(R.id.button4HW);
        Button homeWork5 = findViewById(R.id.button5HW);
        Button homeWork6 = findViewById(R.id.button6HW);
        Button homeWork7 = findViewById(R.id.button7HW);
        Button homework8 = findViewById(R.id.button8HW);
        Button homework9 = findViewById(R.id.button9HW);
        homeWork1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Prime_Activity.this, Main1Activity.class);
                startActivity(intent);
            }
        });
        homeWork2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Prime_Activity.this, Main2Activity.class);
                startActivity(intent);
            }
        });
        homeWork3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Prime_Activity.this, Main3Activity.class);
                startActivity(intent);
            }
        });
        homeWork4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Prime_Activity.this, Main4Activity.class);
                startActivity(intent);
            }
        });
        homeWork5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Prime_Activity.this, Main5Activity.class);
                startActivity(intent);
            }
        });
        homeWork6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Prime_Activity.this, Main6Activity.class);
                startActivity(intent);
            }
        });
        homeWork7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Prime_Activity.this, Main7Activity.class);
                startActivity(intent);
            }
        });
        homework8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Prime_Activity.this, Main8Activity.class);
                startActivity(intent);
            }
        });
        homework9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Prime_Activity.this, Main9Activity.class);
                startActivity(intent);
            }
        });
    }
}
