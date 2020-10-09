package com.example.mysympleapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mysympleapplication.viewModel.MyViewModel;

public class MainActivity extends AppCompatActivity {
    public static final int RUN_PERMISSION_REQUEST = 125;
    MyViewModel myViewModel;
    TextView bestTemp;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bestTemp = findViewById(R.id.best_weather);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "permission ok", Toast.LENGTH_LONG).show();
            //    myViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MyViewModel.class);
            onObserveTempLiveData();
        } else requestPermision();

    }

    void requestPermision() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RUN_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RUN_PERMISSION_REQUEST && grantResults.length>0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onObserveTempLiveData();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void onObserveTempLiveData() {
        myViewModel = new MyViewModel(this);
        myViewModel.getTemp().observe(this, integer -> bestTemp.setText(integer.toString()));     // получаю в активити конечное значение и задаю им атрибут во вьюхе
    }

}
