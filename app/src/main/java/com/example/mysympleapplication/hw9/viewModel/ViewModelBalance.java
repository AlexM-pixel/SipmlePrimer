package com.example.mysympleapplication.hw9.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mysympleapplication.hw9.MyAppDataBase;
import com.example.mysympleapplication.hw9.SpendDao;
import com.example.mysympleapplication.hw9.model.Balance;

public class ViewModelBalance extends ViewModel {
public LiveData<Balance> balanceLiveData= MyAppDataBase.getInstance().balanceDao().getBalance();

}
