package com.example.mysympleapplication.hw9.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mysympleapplication.hw9.MyAppDataBase;
import com.example.mysympleapplication.hw9.Spend;
import com.example.mysympleapplication.hw9.SpendDao;
import com.example.mysympleapplication.hw9.model.Balance;

import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class ViewModelBalance extends ViewModel {
public LiveData<Balance> balanceLiveData= MyAppDataBase.getInstance().balanceDao().getBalance();
public MutableLiveData<Integer> count= new MutableLiveData<>();

    public void setCount(Integer count) {
        this.count.setValue(count);
    }
}
