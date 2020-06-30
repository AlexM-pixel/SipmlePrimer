package com.example.mysympleapplication.hw9;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.mysympleapplication.R;

public class Main9Activity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_SMS = 99;
    public static final String APP_PREFERENCES = "myPreference";
    public static final String PREFERENCES_FROM = "from";
    public static final String PREFERENCES_VALUE = "value";
    SharedPreferences.OnSharedPreferenceChangeListener changeListener;
    SharedPreferences mSettings;
    TextView textViewSms;
    TextView exapleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main9);
        textViewSms = findViewById(R.id.sms_textView);
        exapleText = findViewById(R.id.example_textView);
        mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        textViewSms.setText(mSettings.getString(PREFERENCES_FROM, ""));
        exapleText.setText(mSettings.getString(PREFERENCES_VALUE, ""));
        Log.e("AScs", "onCreate() " + mSettings.getString(PREFERENCES_FROM, ""));

        if (!isSmsPermissionGranted()) {
            ActivityCompat.requestPermissions(Main9Activity.this,
                    new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, PERMISSION_REQUEST_SMS);
        }
       changeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                textViewSms.setText(mSettings.getString(PREFERENCES_FROM, ""));
                exapleText.setText(mSettings.getString(PREFERENCES_VALUE, ""));
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSettings.registerOnSharedPreferenceChangeListener(changeListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mSettings.unregisterOnSharedPreferenceChangeListener(changeListener);
    }

    public boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }


}
