package com.example.mysympleapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        button = findViewById(R.id.tryAgain);
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
            button.setText(R.string.button_next);
            findViewById(R.id.linearLayout).setBackgroundColor(Color.GREEN);
        }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

    }
}
