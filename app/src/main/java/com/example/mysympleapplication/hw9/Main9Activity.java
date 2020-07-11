package com.example.mysympleapplication.hw9;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.mysympleapplication.R;

import java.util.ArrayList;
import java.util.List;

public class Main9Activity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_SMS = 99;
    public static final String DATE = "choice_date";
    private List<SumSpendsOfMonth> cardViewArrayList;
    private SpendMonthAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main9);
        RecyclerView recyclerView = findViewById(R.id.recycler_for_mounth_spends);
        cardViewArrayList = new ArrayList<>();
        getItemForMonthSpendsSpisok();
        adapter = new SpendMonthAdapter(cardViewArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setSpendMonthListener(new SpendMonthAdapter.SpendMonthListener() {
            @Override
            public void onSpendMonthClickListener(String date) {
                Intent intent = new Intent(Main9Activity.this, DetailMonthActivity.class);
                intent.putExtra(DATE,date);
                startActivity(intent);
            }
        });

        Log.e("AScs", "onCreate() " + MyAppDataBase.getInstance().spendDao().getAll().size());
        if (!isSmsPermissionGranted()) {
            ActivityCompat.requestPermissions(Main9Activity.this,
                    new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, PERMISSION_REQUEST_SMS);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void getItemForMonthSpendsSpisok() {
        cardViewArrayList = MyAppDataBase.getInstance().spendDao().getSumMonth();
    }
}
