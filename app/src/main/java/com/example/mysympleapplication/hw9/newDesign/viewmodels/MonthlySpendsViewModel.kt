package com.example.mysympleapplication.hw9.newDesign.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FirestorageRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetCategoryPayUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetMonthlySpendsUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class MonthlySpendsViewModel @Inject constructor(
    private val getMonthlySpendsUseCase: GetMonthlySpendsUseCase,
) : ViewModel() {
    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData
    private val _monthlySpendsLiveData = MutableLiveData<List<Spend>>()
    val monthlySpendsLiveData: MutableLiveData<List<Spend>> get() = _monthlySpendsLiveData


    fun getMonthlySpends(month: String) {
        getMonthlySpendsUseCase(dateMonth = month).onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    _stateLiveData.value = State.SUCCESS
                    _monthlySpendsLiveData.value = it.data ?: mutableListOf()
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



