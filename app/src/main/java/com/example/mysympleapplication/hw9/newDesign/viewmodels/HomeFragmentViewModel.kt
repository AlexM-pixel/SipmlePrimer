package com.example.mysympleapplication.hw9.newDesign.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetBalanceUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetMonthlyExpensesUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class HomeFragmentViewModel @Inject constructor(
    private val useCaseGetMonthlyExpenses: GetMonthlyExpensesUseCase,
    private val getBalanceUseCase: GetBalanceUseCase
) :
    ViewModel() {
    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData
    private val _sumSpendsLiveData = MutableLiveData<List<SumSpendsOfMonth>>()
    val sumSpendsLiveData: MutableLiveData<List<SumSpendsOfMonth>> get() = _sumSpendsLiveData
    private val _balanceLiveData = MutableLiveData<Balance?>()
    val balanceLiveData: MutableLiveData<Balance?> get() = _balanceLiveData

    fun getMonthlyExpenses() {
        useCaseGetMonthlyExpenses().onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    _stateLiveData.value = State.SUCCESS
                    _sumSpendsLiveData.value = it.data ?: mutableListOf()
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("homeViewModel", "Error: ${it.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getInformationCards() {

    }

    fun getBalance() {
        getBalanceUseCase().onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    _stateLiveData.value = State.SUCCESS
                    _balanceLiveData.postValue(it.data)
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                }
            }
        }.launchIn(viewModelScope)
    }

}