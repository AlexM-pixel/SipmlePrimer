package com.example.mysympleapplication.hw1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysympleapplication.R;

import java.util.Timer;
import java.util.TimerTask;

public class Main1Activity extends AppCompatActivity {

    private TextView firstValue;
    private TextView secondValue;
    private EditText userInpyt;
    private int numOne;
    private int numSecond;
    private int timerN;
    private int valuefirst;
    private int valueSecond;
    private int level = 1;
    private Timer mTimer;
    private TextView timerView;
    private TextView levelView;
    public static final int ACTIVITY_NUMBER = 1;
    public static final String VALUE_KEY = "keyfirst";
    public static final String RESULT_KEY = "keysecond";
    private static final String NUM_ONE = "keyOne";
    private static final String NUM_TWO = "keyTwo";
    private static final String TIMER_VALUE = "timerV";
    private static final String LEVEL_VALUE = "level_key";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstValue = (TextView) findViewById(R.id.firstValue);
        secondValue = (TextView) findViewById(R.id.secondValue);
        userInpyt = (EditText) findViewById(R.id.aswer);
        timerView = (TextView) findViewById(R.id.timerView);
        levelView = (TextView) findViewById(R.id.level_view);
        levelView.setText("Уровень: " + level);                            //отобразил текущий уровень
        valuefirst = 1;
        valueSecond = 2;
        if (savedInstanceState != null) {                                     // восстановление состояния при повороте экрана
            numOne = savedInstanceState.getInt(NUM_ONE);
            numSecond = savedInstanceState.getInt(NUM_TWO);
            timerN = savedInstanceState.getInt(TIMER_VALUE);
            level = savedInstanceState.getInt(LEVEL_VALUE);
            levelView.setText("Уровень: " + level);
            firstValue.setText(String.valueOf(numOne));
            secondValue.setText(String.valueOf(numSecond));
            timerView.setText(String.valueOf(timerN));
        } else {
            timerN = 10;
            setMethod();                                                     // запуск метода генерации случайных чисел
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        userInpyt.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);    // клавиатура
        if (mTimer == null) {
            timerView.setTextColor(Color.GRAY);
            timerView.setText(String.valueOf(timerN));
            mTimer = new Timer();
            MyTimerTask mMyTimerTask = new MyTimerTask();
            mTimer.schedule(mMyTimerTask, 0, 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    timerView.setText(String.valueOf(timerN));
                    if (timerN == 0) {
                        Intent intent = new Intent(Main1Activity.this, GameOverActivity.class);
                        startActivity(intent);                                                  // время вышло, запуск активити
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
        if (resultCode == SecondActivity.KEY_NEXT) {              // если во втором активити была нажата кнопка следующий уорвень
            userInpyt.setText("");
            valuefirst = valuefirst + 3;
            valueSecond = valueSecond + 3;
            level++;
            String levelStr = "Уровень: " + level;
            levelView.setText(levelStr);
            setMethod();
        } else if (resultCode == RESULT_OK) {              // если во втором активити ответ правильный, заполняю пример новыми числами
            userInpyt.setText("");
            setMethod();
        }
    }

    public void goAnswer(View view) {
        if (userInpyt.getText().length() > 0) {
            mTimer.cancel();
            mTimer = null;
            timerN = 10;
            int result = numOne + numSecond;
            int userValue = Integer.parseInt(userInpyt.getText().toString());
            Intent intent = new Intent(this, SecondActivity.class);
            intent.putExtra(RESULT_KEY, result);                                 // правильный результат вычисления
            intent.putExtra(VALUE_KEY, userValue);                             // значение пользователя
            startActivityForResult(intent, ACTIVITY_NUMBER);
        } else {
            Toast.makeText(this, "Введите ответ", Toast.LENGTH_SHORT).show();
        }
    }

    public void setMethod() {
        numOne = (int) (Math.random() * 10 + valuefirst);
        numSecond = (int) (Math.random() * 20 + valueSecond);
        firstValue.setText(String.valueOf(numOne));
        secondValue.setText(String.valueOf(numSecond));

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {      // сохранил состояние переменных
        super.onSaveInstanceState(outState);
        outState.putInt(NUM_ONE, numOne);
        outState.putInt(NUM_TWO, numSecond);
        outState.putInt(TIMER_VALUE, timerN);
        outState.putInt(LEVEL_VALUE, level);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
