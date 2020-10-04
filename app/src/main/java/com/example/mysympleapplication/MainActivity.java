package com.example.mysympleapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysympleapplication.model.SampleWeather;
import com.example.mysympleapplication.viewModel.MyLiveData;

public class MainActivity extends AppCompatActivity {
    public static final int RUN_PERMISSION_REQUEST = 125;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
TextView bestTemp=findViewById(R.id.best_weather);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "permission ok", Toast.LENGTH_LONG).show();
            MyLiveData myLiveData = new MyLiveData(this);
            myLiveData.observe(this, integer -> bestTemp.setText(integer.toString()));
        } else requestPermision();

    }

    void requestPermision() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RUN_PERMISSION_REQUEST);
    }
}
