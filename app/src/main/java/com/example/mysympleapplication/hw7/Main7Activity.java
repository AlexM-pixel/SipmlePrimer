package com.example.mysympleapplication.hw7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mysympleapplication.R;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class Main7Activity extends AppCompatActivity {
    TextView countFiles;
    EditText nameFile;
    public static final int SEARCH_IN_PROGRESS = -1;
    String[] allFiles;
    Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        nameFile = findViewById(R.id.searchFile);
        countFiles = findViewById(R.id.count_files);
        allFiles = fileList();
        handler = new Handler() {
            @SuppressLint("SetTextI18n")
            public void handleMessage(Message msg) {
                if (msg.what == SEARCH_IN_PROGRESS) {
                    countFiles.setText("я ищу!");
                } else {
                    countFiles.setText("найдено файлов: " + msg.what);
                }
            }
        };

        nameFile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new Thread(new Runnable() {
                    public void run() {
                        int cound = 0;
                        handler.sendEmptyMessage(SEARCH_IN_PROGRESS);
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        for (String fileName : allFiles) {
                            if (fileName.contains("." + nameFile.getText())) {
                                cound++;
                                Log.d("onTextChanged", "i = " + cound);
                            }
                            handler.sendEmptyMessage(cound);
                        }
                    }
                }).start();
            }


            @Override
            public void afterTextChanged(Editable s) {
                //  Log.d("onTextChanged", "after " + cound);
            }
        });
    }
}
