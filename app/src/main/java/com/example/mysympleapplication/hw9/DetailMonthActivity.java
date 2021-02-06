package com.example.mysympleapplication.hw9;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.mysympleapplication.R;
import com.example.mysympleapplication.hw9.view.iu.BaseActivity;
import com.example.mysympleapplication.hw9.viewModel.MyViewModel;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.List;

public class DetailMonthActivity extends BaseActivity {
    public static final String NAME_SPEND = "nameSpend";
    private String date;
    public static final String DATE = "date_spends";
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
        Slidr.attach(this);             // слайд обратно
        RecyclerView recyclerView = findViewById(R.id.detailmonth_recycler);
        calendarSpendsList = new ArrayList<>();
        MyViewModel myViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MyViewModel.class);
        adapter = new DetailMonthAdapter(calendarSpendsList);
        myViewModel.data.setValue(date);
        myViewModel.listLiveCalendarSpends.observe(this, calendarSpends -> adapter.setDetailMonthList(calendarSpends));
    //    getItemForDetailSpendsSpisok();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setListenerDetail(new DetailMonthAdapter.DetailMonthListener() {
            @Override
            public void onDetailSpendClickListener(String date, String name) {
                Intent intent = new Intent(DetailMonthActivity.this, DetailActivity.class);
                intent.putExtra(DATE, date);
                intent.putExtra(NAME_SPEND, name);
                startActivity(intent);
            }
        });
    }

    private void getItemForDetailSpendsSpisok() {
        //  calendarSpendsList=MyAppDataBase.getInstance().spendDao().getMonthSpends(date);

    }
}
