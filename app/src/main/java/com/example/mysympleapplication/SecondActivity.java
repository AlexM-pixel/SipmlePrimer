package com.example.mysympleapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class SecondActivity extends AppCompatActivity {
    Button buttonCurrent;
    Button buttonNextLevel;
    public static final int KEY_NEXT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        buttonCurrent = findViewById(R.id.tryAgain);
        buttonNextLevel = findViewById(R.id.button_next_level);
        verificationResult();
    }


    public void verificationResult() {
        Bundle arguments = getIntent().getExtras();
        int userValue = arguments.getInt(MainActivity.VALUE_KEY);
        int result = arguments.getInt(MainActivity.RESULT_KEY);
        if (userValue == result) {
            setResult(RESULT_OK);
            TextView textResult = findViewById(R.id.result);
            textResult.setText(R.string.true_answer);
            textResult.setTextColor(Color.GREEN);
            buttonCurrent.setText(R.string.button_next);
            findViewById(R.id.linearLayout).setBackgroundColor(Color.GREEN);
            buttonNextLevel.setVisibility(View.VISIBLE);
        }
        buttonCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonNextLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.putExtra(KEY_NEXT, "nextLevel");
                setResult(KEY_NEXT);
                finish();
            }
        });
    }
}
