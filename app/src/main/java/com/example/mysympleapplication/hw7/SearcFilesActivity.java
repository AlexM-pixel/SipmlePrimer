package com.example.mysympleapplication.hw7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mysympleapplication.R;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class SearcFilesActivity extends AppCompatActivity {
    TextView countFiles;
    EditText nameFile;
    CountFilesReceiver countFilesReceiver;
    public static final int RUN_PERMISSION_REQUEST = 123;
    public static final String LIST_FILES = "all_files";
    public static final String NAME_FILE = "fileName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        nameFile = findViewById(R.id.searchFile);
        countFiles = findViewById(R.id.count_files);
        Button buttonStartSearch = findViewById(R.id.button_start_search);
        buttonStartSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countFiles.setText("ищем!");
                Intent intent = new Intent(SearcFilesActivity.this, SearchService.class);
                int permissionStatus = ContextCompat.checkSelfPermission(SearcFilesActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    intent.putExtra(LIST_FILES, (String.valueOf(Environment.getRootDirectory())));
                    intent.putExtra(NAME_FILE, nameFile.getText().toString());
                    startService(intent);
                } else {
                    ActivityCompat.requestPermissions(SearcFilesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RUN_PERMISSION_REQUEST);
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        countFilesReceiver = new CountFilesReceiver();
        registerReceiver(countFilesReceiver, new IntentFilter(SearchService.SEND_COUNT_FOR_ACTIVITY));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RUN_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                Intent intent = new Intent(SearcFilesActivity.this, SearchService.class);
                intent.putExtra(LIST_FILES, (String.valueOf(Environment.getRootDirectory())));
                intent.putExtra(NAME_FILE, nameFile.getText().toString());
                startService(intent);
            } else {
                // permission denied
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countFilesReceiver!= null) {
            unregisterReceiver(countFilesReceiver);
        }
    }

    class CountFilesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int count_files = intent.getIntExtra(SearchService.COUNT_FILES,0);
            countFiles.setText(String.valueOf(count_files));
        }
    }
}
