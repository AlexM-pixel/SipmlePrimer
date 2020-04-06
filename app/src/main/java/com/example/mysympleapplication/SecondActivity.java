package com.example.mysympleapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {
    private Button buttonCurrent;
    private Button buttonNextLevel;
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
            textResult.setTextColor(getResources().getColor(R.color.colorGreen));
            buttonCurrent.setText(R.string.button_next);
            findViewById(R.id.linearLayout).setBackgroundColor(getResources().getColor(R.color.colorGreen));
            buttonNextLevel.setVisibility(View.VISIBLE);
            buttonNextLevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(KEY_NEXT);
                    finish();
                }
            });
        } else {
            buttonNextLevel.setText(R.string.statistic);
            buttonNextLevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SecondActivity.this, Statistic.class);
                    startActivity(intent);
                }
            });

        }
        buttonCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
