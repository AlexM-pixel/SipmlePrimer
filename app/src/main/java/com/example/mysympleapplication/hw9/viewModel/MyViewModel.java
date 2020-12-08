package com.example.mysympleapplication.hw9.viewModel;

import android.util.Pair;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mysympleapplication.hw9.CalendarSpends;
import com.example.mysympleapplication.hw9.MyAppDataBase;
import com.example.mysympleapplication.hw9.SpendDao;
import com.example.mysympleapplication.hw9.SumSpendsOfMonth;

import java.util.List;

public class MyViewModel extends ViewModel {
    private SpendDao spendDao = MyAppDataBase.getInstance().spendDao();
    public LiveData<List<SumSpendsOfMonth>> listLiveData = spendDao.getSumMonth();
    public MutableLiveData<String> data = new MutableLiveData<>();
    public MutableLiveData<Pair<String,String>> detailData = new MutableLiveData<>();
    public LiveData<List<CalendarSpends>> listLiveCalendarSpends= Transformations.switchMap(data, new Function<String, LiveData<List<CalendarSpends>>>() {
        @Override
        public LiveData<List<CalendarSpends>> apply(String input) {
            return spendDao.getMonthSpends(input);
        }
    });
    public LiveData<List<CalendarSpends>> detailCalendarSpends=Transformations.switchMap(detailData,input -> spendDao.getAll(input.first, input.second));

}
