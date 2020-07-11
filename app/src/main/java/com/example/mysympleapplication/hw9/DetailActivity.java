package com.example.mysympleapplication.hw9;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.mysympleapplication.R;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private List<Spend> spendList;
    DetailMonthAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            String date = arguments.getString(Main9Activity.DATE);
            spendList = MyAppDataBase.getInstance().spendDao().getAll(date);
        }
        RecyclerView recyclerView = findViewById(R.id.recycler_for_detail_spends);
adapter=new DetailMonthAdapter(spendList);
    }

}
