package com.example.mysympleapplication.hw9.newDesign.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetSpendsByNameUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class DetailMonthlyViewModel @Inject constructor(private val spendsByNameUseCase: GetSpendsByNameUseCase) :
    ViewModel() {
    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData
    private val _detailSpendsByNameLiveData = MutableLiveData<List<Spend>>()
    val detailSpendsByNameLiveData: MutableLiveData<List<Spend>> get() = _detailSpendsByNameLiveData

    fun getMonthlySpends(name:String,month: String) {
        spendsByNameUseCase(name = name, dateMonth = month).onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    _stateLiveData.value = State.SUCCESS
                    _detailSpendsByNameLiveData.value = it.data ?: mutableListOf()
                    Log.e("getMonthlySpendsUseCase", "spendsSuccess")
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("homeViewModel", "Error: ${it.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

}