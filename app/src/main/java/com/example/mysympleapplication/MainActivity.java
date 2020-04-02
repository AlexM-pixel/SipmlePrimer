package com.example.mysympleapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Button answer;
    TextView firstValue;
    TextView secondValue;
    EditText userInpyt;
    int numOne;
    int numSecond;
    int timerN;
    Context context = this;
    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    TextView timerView;
    public static final String VALUE_KEY = "keyfirst";
    public static final String RESULT_KEY = "keysecond";
    public static final String NUM_ONE = "keyOne";
    public static final String NUM_TWO = "keyTwo";
    public static final String TIMER_VALUE = "timerV";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        answer = (Button) findViewById(R.id.button);
        firstValue = (TextView) findViewById(R.id.firstValue);
        secondValue = (TextView) findViewById(R.id.secondValue);
        userInpyt = (EditText) findViewById(R.id.aswer);
        timerView = (TextView) findViewById(R.id.timerView);
        if (savedInstanceState != null) {                                     // восстановление состояния при повороте экрана
            numOne = savedInstanceState.getInt(NUM_ONE);
            numSecond = savedInstanceState.getInt(NUM_TWO);
            timerN = savedInstanceState.getInt(TIMER_VALUE);
            firstValue.setText(String.valueOf(numOne));
            secondValue.setText(String.valueOf(numSecond));
            timerView.setText(String.valueOf(timerN));
        } else {
            timerN=10;
            setMethod();                                                     // запуск метода генерации случайных чисел
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTimer == null) {
            timerView.setTextColor(Color.GREEN);
            timerView.setText(String.valueOf(timerN));
            mTimer = new Timer();
            mMyTimerTask = new MyTimerTask();
            mTimer.schedule(mMyTimerTask, 0, 1000);
        }
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    timerView.setText(String.valueOf(timerN));
                    if (timerN == 0) {
                        Intent intent = new Intent(context, GameOverActivity.class);
                        startActivity(intent);                                                  // время вышло, запусу активити
                        timerN = 10;
                        mTimer.cancel();
                        mTimer = null;
                    }
                    if (timerN <= 3) {
                        timerView.setTextColor(Color.RED);
                    }
                    timerN--;
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {          // получаю результат из второго активити
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {                                                                 // если во втором активити ответ правильный, заполняю пример новыми числами
            userInpyt.setText("");
            setMethod();
        }
    }

    public void goAnswer(View view) {
        if (userInpyt.getText().length() > 0) {
            mTimer.cancel();
            mTimer = null;
            timerN=10;
            int result = numOne + numSecond;
            int userValue = Integer.parseInt(userInpyt.getText().toString());
            Intent intent = new Intent(this, SecondActivity.class);
            intent.putExtra(RESULT_KEY, result);                                 // правильный результат вычисления
            intent.putExtra(VALUE_KEY, userValue);                             // значение пользователя
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(this, "Введите ответ", Toast.LENGTH_SHORT).show();
        }
    }

    public void setMethod() {
        numOne = (int) (Math.random() * 10 + 1);
        numSecond = (int) (Math.random() * 20 + 1);
        firstValue.setText(String.valueOf(numOne));
        secondValue.setText(String.valueOf(numSecond));

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {      // сохранил состояние переменных
        super.onSaveInstanceState(outState);
        outState.putInt(NUM_ONE, numOne);
        outState.putInt(NUM_TWO, numSecond);
        outState.putInt(TIMER_VALUE, timerN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer!=null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
