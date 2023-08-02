package com.example.mysympleapplication.hw9.newDesign.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetBalanceUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetFriendExpensesUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetFriendsBalanceUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetUserExpensesUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class StatisticViewModel @Inject constructor(
    private val getExpensesUserUseCase: GetUserExpensesUseCase,
    private val getBalanceUserUseCase: GetBalanceUseCase,
    private val getExpensesFriendUseCase: GetFriendExpensesUseCase,
    private val getBalanceFriendUseCase: GetFriendsBalanceUseCase,
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData

    private val _userSpendsLiveData = MutableLiveData<SumSpendsOfMonth?>()
    val userSpendsLiveData: MutableLiveData<SumSpendsOfMonth?> get() = _userSpendsLiveData
    private val _friendSpendsLiveData = MutableLiveData<SumSpendsOfMonth>()
    val friendSpendsLiveData: MutableLiveData<SumSpendsOfMonth> get() = _friendSpendsLiveData

    private val _friendsBalanceLiveData = MutableLiveData<Balance?>()
    val friendsBalanceLiveData: MutableLiveData<Balance?> get() = _friendsBalanceLiveData
    private val _userBalanceLiveData = MutableLiveData<Balance?>()
    val userBalanceLiveData: MutableLiveData<Balance?> get() = _userBalanceLiveData

    fun getUserExpenses() {
        getExpensesUserUseCase(getDate()).onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    _userSpendsLiveData.value = it.data
                }
                is Resource.Error -> {
                    State.ERROR.stateDescription = it.message.toString()
                    _stateLiveData.value = State.ERROR
                    _userSpendsLiveData.value = SumSpendsOfMonth(getDate(), 0f)
                    Log.e("StatisticViewModel", "Error: ${it.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getFriendsExpenses() {
        viewModelScope.launch {
            _stateLiveData.value = State.LOADING
            when (val res = getExpensesFriendUseCase(getDateFriend(), MainPrefs.mailFriend)) {
                is Result.Value -> {
                    val sumSpends = SumSpendsOfMonth(
                        res.value.firstOrNull()?.date,
                        res.value.sumOf { it.value.toFloat() })
                    _friendSpendsLiveData.value = sumSpends
                    Log.e(
                        "getFriendsExpenses",
                        "getExpensesFriendUseCase Success: ${res.value.size}"
                    )
                    _stateLiveData.value = State.SUCCESS
                }
                is Result.Error -> {
                    State.ERROR.stateDescription = res.error.message.toString()
                    _stateLiveData.value = State.ERROR
                    Log.e("getFriendsExpenses", "getExpensesFriendUseCase ERROR: " + res.error)

                }
            }
        }
    }


    fun getFriendBalance() {
        getBalanceFriendUseCase(MainPrefs.mailFriend).onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    _friendsBalanceLiveData.value = it.data
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("getFriendBalance", "Error: ${it.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getBalance() {
        getBalanceUserUseCase().onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    _userBalanceLiveData.postValue(it.data)
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getDate(): String {
        val getdate = Date()
        val newDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return newDateFormat.format(getdate);
    }

    private fun getDateFriend(): String {
        val getdate = Date()
        val newDateFormat = SimpleDateFormat("yyyy-MM") //форматирование для запроса firestore
        return newDateFormat.format(getdate);
    }


    private fun <T> Iterable<T>.sumOf(selector: (T) -> Float): Float {
        var sum = 0f
        for (element in this) {
            Log.e(
                "SumSpendsOfMonth", "element: == $element "
            )
            sum += selector(element)
        }
        return sum
    }

}