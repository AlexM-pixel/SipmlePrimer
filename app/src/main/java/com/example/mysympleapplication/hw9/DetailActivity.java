package com.example.mysympleapplication.hw9;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.mysympleapplication.R;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private List<CalendarSpends> spendList;
    DetailMonthAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle arguments = getIntent().getExtras();
        Log.e("AScs", "onCreate() " + arguments.getString(DetailMonthActivity.DATE));

        if (arguments != null) {
            String date = arguments.getString(DetailMonthActivity.DATE);
            String name = arguments.getString(DetailMonthActivity.NAME_SPEND);
            spendList = MyAppDataBase.getInstance().spendDao().getAll(date, name);
        }
        RecyclerView recyclerView = findViewById(R.id.recycler_for_detail_spends);
        adapter = new DetailMonthAdapter(spendList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

}
