package com.example.mysympleapplication.hw9.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mysympleapplication.hw9.MyAppDataBase;
import com.example.mysympleapplication.hw9.Spend;
import com.example.mysympleapplication.hw9.SpendDao;
import com.example.mysympleapplication.hw9.SumSpendsOfMonth;
import com.example.mysympleapplication.hw9.model.Balance;
import com.example.mysympleapplication.hw9.model.FriendsSumValue;

import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class ViewModelBalance extends ViewModel {
    public LiveData<Balance> balanceLiveData = MyAppDataBase.getInstance().balanceDao().getBalance();
    private List<SumSpendsOfMonth> userSumSpendsOfMonth = MyAppDataBase.getInstance().spendDao().getSumMonthValue();


    public SumSpendsOfMonth getSumOfMonthSpends(String month) {
        if (userSumSpendsOfMonth != null) {
            for (SumSpendsOfMonth user : userSumSpendsOfMonth) {
                if (month.equals(user.getDateM())) {
                    Log.e("AScs", "user: " + user.getDateM());
                    return user;
                }
            }
        } else {
            Log.e("AScs", "userSumSpendsOfMonth == null");
            return null;
        }
        return new SumSpendsOfMonth("00", 0);
    }

}
