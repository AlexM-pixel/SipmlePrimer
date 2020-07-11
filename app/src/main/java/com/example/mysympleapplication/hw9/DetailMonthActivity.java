package com.example.mysympleapplication.hw9;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.mysympleapplication.R;

import java.util.ArrayList;
import java.util.List;

public class DetailMonthActivity extends AppCompatActivity {
    private String date;
    private List<CalendarSpends> calendarSpendsList;
    DetailMonthAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_month);
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            date = arguments.getString(Main9Activity.DATE);
        }
        RecyclerView recyclerView=findViewById(R.id.detailmonth_recycler);
        calendarSpendsList= new ArrayList<>();
        getItemForDetailSpendsSpisok();
        adapter= new DetailMonthAdapter(calendarSpendsList);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    private void getItemForDetailSpendsSpisok() {
        calendarSpendsList=MyAppDataBase.getInstance().spendDao().getMonthSpends(date);
    }
}
