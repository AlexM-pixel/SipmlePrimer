package com.example.mysympleapplication.hw9.newDesign.viewmodels

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.domain.model.PairSpends
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetBalanceUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetFriendExpensesUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetFriendsBalanceUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetMonthlySpendsUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetUserExpensesUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class StatisticViewModel @Inject constructor(
    private val getExpensesUserUseCase: GetUserExpensesUseCase,
    private val getBalanceUserUseCase: GetBalanceUseCase,
    private val getExpensesFriendUseCase: GetFriendExpensesUseCase,
    private val getBalanceFriendUseCase: GetFriendsBalanceUseCase,
    private val getMonthlySpendsUseCase: GetMonthlySpendsUseCase
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


    private val _monthSpendsLiveDataUser = MutableLiveData<List<Spend>>()
    private val _monthSpendsLiveDataFriend = MutableLiveData<List<Spend>>()
    private val pairLiveData =
        MediatorLiveData<Pair<List<Spend>?, List<Spend>?>>()
    var usersSpendsListLiveData: LiveData<List<PairSpends>> =
        MutableLiveData()

    init {
        getMonthlyUserSpends()       // запустил метод для получения спендов чтобы засетить: _sumSpendsLiveDataUser
        pairLiveData.addSource(_monthSpendsLiveDataUser) { userList ->
            pairLiveData.value = Pair(userList, pairLiveData.value?.second)
        }
        pairLiveData.addSource(_monthSpendsLiveDataFriend) { friendList ->
            pairLiveData.value = Pair(
                pairLiveData.value?.first,
                friendList
            )                                               // осталось из этого пэера получить список новых обьектов
        }
        usersSpendsListLiveData = pairLiveData.switchMap { spend -> getUsersSpendsList(spend) }
    }


    fun getUserExpenses() {
        getExpensesUserUseCase(getDate()).onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }

                is Resource.Success -> {
                    _userSpendsLiveData.value = it.data
                    Log.e("StatisticViewModel", "Success: ${it.data?.dateM}")
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

    @SuppressLint("NullSafeMutableLiveData")
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
                        "getExpensesFriendUseCase Success: ${res.value.size}, res.value= ${sumSpends.value_spends}, res.date= ${sumSpends.dateM}"
                    )
                    _stateLiveData.value = State.SUCCESS

                    val friendSpendList = mutableListOf<Spend>()
                    res.value.groupBy { it.spendName }
                        .map { (key, pairList) ->     // получаю новый список
                            val valSpend = pairList.sumOf { it.value.toFloat() }
                            val spend = Spend(
                                0,
                                key,
                                valSpend.toString(),
                                pairList.firstOrNull()?.date?:"0",
                                pairList.firstOrNull()?.url ?: ""
                            )
                            friendSpendList.add(spend)
                        }
                    Log.e("!!!getFriendses!!", "friendSpendList: ${friendSpendList}")
                    _monthSpendsLiveDataFriend.value =
                        friendSpendList     // задал список покупок суммированых по каждой категории
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

    private fun getUsersSpendsList(spends: Pair<List<Spend>?, List<Spend>?>): MutableLiveData<List<PairSpends>> { // получение парных расходов по категориям
        val pairSpendsList = mutableListOf<PairSpends>()
        var para = true

        spends.first?.forEach { user ->
            if (!spends.second.isNullOrEmpty()) {
                for (j in spends.second!!.indices) {
                    if (user.spendName == (spends.second?.get(j)?.spendName ?: "")) {
                        val pairSpends =
                            PairSpends(
                                valueUser = user.value.toFloat(),
                                valueFriend = spends.second?.getOrNull(j)?.value?.toFloatOrNull()
                                    ?: 0f, nameSpend = user.spendName, url = user.url
                            )
                        pairSpendsList.add(pairSpends)
                        para = true
                        break
                    } else para = false
                }
                if (!para) {
                    val pairSpends = PairSpends(user.value.toFloat(), 0f, user.spendName, user.url)
                    pairSpendsList.add(pairSpends)
                }
            } else {
                Log.d("!!!friend=null!!!", spends.second?.size.toString())
                // если покупок твоего френда еще не было надо создать список хоть с твоими
                val pairSpends =
                    PairSpends(
                        valueUser = user.value.toFloat(),
                        valueFriend = 0f, nameSpend = user.spendName, url = user.url
                    )
                pairSpendsList.add(pairSpends)
            }
        }

        spends.second?.forEach { friend ->
            if (!spends.first.isNullOrEmpty()) {
                for (j in spends.first!!.indices) {
                    if (friend.spendName == (spends.first?.get(j)?.spendName ?: "")) {
                        para = true
                        break
                    } else para = false
                }
                if (!para) {
                    val pairSpends =
                        PairSpends(
                            valueUser = 0f,
                            valueFriend = friend.value.toFloatOrNull() ?: 0f,
                            friend.spendName, url = friend.url
                        )
                    pairSpendsList.add(pairSpends)
                }
            } else {
                Log.d("!!!user=null!!!", spends.first?.size.toString())
                val pairSpends =
                    PairSpends(
                        valueUser = 0f,
                        valueFriend = friend.value.toFloatOrNull() ?: 0f, nameSpend = friend.spendName, url = friend.url
                    )
                pairSpendsList.add(pairSpends)
            }
        }
        val pairSpendsLiveData = MutableLiveData<List<PairSpends>>()
        pairSpendsLiveData.value = pairSpendsList
        return pairSpendsLiveData
    }


    private fun getMonthlyUserSpends() {                                             // получаю сумму расходов по каждому магазину для пользователя
        getMonthlySpendsUseCase(dateMonth = getDateDbFormat()).onEach { res ->
            when (res) {
                is Resource.Success -> {
                    _monthSpendsLiveDataUser.value = res.data ?: mutableListOf()
                    Log.e("getMonthlyUserSpends", "spendsSuccess listSpends: ${res.data}")
                }

                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("StatisticViewModel", "getMonthlyUserSpends Error: ${res.message}")
                }

                else -> {}
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

     fun getDateDbFormat(): String {
        val getdate = Date()
        val newDateFormat = SimpleDateFormat("MM-yyyy")
        return newDateFormat.format(getdate)
    }


    private fun <T> Iterable<T>.sumOf(selector: (T) -> Float): Float {
        var sum = 0f
        for (element in this) {
            sum += selector(element)
        }
        return sum
    }


}

