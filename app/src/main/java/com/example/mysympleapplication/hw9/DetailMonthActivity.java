package com.example.mysympleapplication.hw9;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.mysympleapplication.R;

import java.util.ArrayList;
import java.util.List;

public class DetailMonthActivity extends AppCompatActivity {
    public static final String NAME_SPEND = "nameSpend";
    private String date;
    public static final String DATE="date_spends";
    private List<CalendarSpends> calendarSpendsList;
    DetailMonthAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_month);
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            date = arguments.getString(Main9Activity.DATE_MONTH);
        }
        RecyclerView recyclerView=findViewById(R.id.detailmonth_recycler);
        calendarSpendsList= new ArrayList<>();
        getItemForDetailSpendsSpisok();
        adapter= new DetailMonthAdapter(calendarSpendsList);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setListenerDetail(new DetailMonthAdapter.DetailMonthListener() {
            @Override
            public void onDetailSpendClickListener(String date, String name) {
                Intent intent = new Intent(DetailMonthActivity.this, DetailActivity.class);
                intent.putExtra(DATE,date);
                intent.putExtra(NAME_SPEND,name);
                startActivity(intent);
            }
        });
    }
    private void getItemForDetailSpendsSpisok() {
        calendarSpendsList=MyAppDataBase.getInstance().spendDao().getMonthSpends(date);
    }
}
