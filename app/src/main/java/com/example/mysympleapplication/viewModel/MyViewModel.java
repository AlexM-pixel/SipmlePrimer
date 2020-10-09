package com.example.mysympleapplication.viewModel;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;


public class MyViewModel extends ViewModel {
    MyLiveData myLiveData;
    private MutableLiveData<Integer> maxLiveData = new MutableLiveData<>();


    public MyViewModel(Context context) {
        myLiveData = new MyLiveData(context);
        myLiveData.observe((LifecycleOwner) context, weatherPair -> {
            if (weatherPair.first != null && weatherPair.second != null) {
                int current_temp = weatherPair.first.getFact().getTemp();
                int other_temp = weatherPair.second.getFact().getTemp();
                maxLiveData.setValue(Math.max(current_temp, other_temp));   // тут в лайв дату которую слушаю в активити занес значение с более высокой температурой
            }
        });
        Log.e("AScs", " / vewModel constructor");
    }

    public LiveData<Integer> getTemp() {
        Log.e("AScs", " / vewModel getTemp");
        return maxLiveData;
    }
}
