package com.example.mysympleapplication.hw9.view.iu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mysympleapplication.R;

import static com.example.mysympleapplication.hw9.Main9Activity.APP_PREFERENCES;

public class BaseActivity extends AppCompatActivity {
    @VisibleForTesting
    public ProgressBar mProgressBar;
    public static final String THEME_STYLE="Theme";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sPref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        int styleTheme = sPref.getInt(THEME_STYLE, R.style.AppTheme);                                // получил текущую тему
        setTheme(styleTheme);                                           // и засетил ее
    }

    public void setProgressBar(int id) {
        mProgressBar = findViewById(id);
    }

    public void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressBar();
    }
}
