package com.example.mysympleapplication.hw9.newDesign.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.domain.model.PairSpends
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.domain.model.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.*
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import kotlinx.coroutines.Dispatchers
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

    // --- Состояния экрана (Инкапсуляция) ---
    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData

    private val _userSpendsLiveData = MutableLiveData<SumSpendsOfMonth?>()
    val userSpendsLiveData: LiveData<SumSpendsOfMonth?> get() = _userSpendsLiveData

    private val _friendSpendsLiveData = MutableLiveData<SumSpendsOfMonth>()
    val friendSpendsLiveData: LiveData<SumSpendsOfMonth> get() = _friendSpendsLiveData

    private val _friendsBalanceLiveData = MutableLiveData<Balance?>()
    val friendsBalanceLiveData: LiveData<Balance?> get() = _friendsBalanceLiveData

    private val _userBalanceLiveData = MutableLiveData<Balance?>()
    val userBalanceLiveData: LiveData<Balance?> get() = _userBalanceLiveData

    // Внутренние списки трат для объединения
    private var localUserSpendsList: List<Spend> = emptyList()
    private var localFriendSpendsList: List<Spend> = emptyList()


    // --- Форматтеры дат (Создаем один раз для экономии ресурсов) ---
    private val dateFormatFull = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateFormatMonthYear = SimpleDateFormat("MM-yyyy", Locale.getDefault())
    private val dateFormatFirestore = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    private val currentDate = Date()

    // --- ИНКАПСУЛЯЦИЯ СПИСКА ---
    private val _usersSpendsListLiveData = MutableLiveData<List<PairSpends>>()
    val usersSpendsListLiveData: LiveData<List<PairSpends>> get() = _usersSpendsListLiveData

    // --- ФЛАГИ ДЛЯ БАРЬЕРА ---
    private var isUserSpendsLoaded = false
    private var isFriendSpendsLoaded = false

    init {

        loadAllData()
    }


     fun loadAllData() {
        _stateLiveData.value = State.LOADING
        // Сбрасываем флаги перед новой загрузкой
        isUserSpendsLoaded = false
        isFriendSpendsLoaded = false

        getMonthlyUserSpends()
        getUserExpenses()
        getFriendBalance()
        getBalance()
        getFriendsExpenses()
    }

    // 1. Твои расходы (Общая сумма)
    fun getUserExpenses() {
        getExpensesUserUseCase(dateFormatFull.format(currentDate)).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    _userSpendsLiveData.value = resource.data
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    // Защита: передаем нули в виде строк, если ошибка
                    _userSpendsLiveData.value = SumSpendsOfMonth(dateFormatFull.format(currentDate), 0f)
                    Log.e("StatisticViewModel", "Error User Expenses: ${resource.message}")
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    // 2. Расходы друга (Сумма + Детальный список)
    fun getFriendsExpenses() {
        viewModelScope.launch(Dispatchers.IO) {
            val friendEmail = MainPrefs.mailFriend

            // Если друга нет, притворяемся, что его данные "загрузились" (они пустые)
            if (friendEmail.isEmpty()) {
                isFriendSpendsLoaded = true
                checkAndMerge()
                return@launch
            }

            when (val res = getExpensesFriendUseCase(dateFormatFirestore.format(currentDate), friendEmail)) {
                is Result.Value -> {
                    // ... (тут твой код суммирования баланса друга, который был раньше) ...
                    val totalFriendSpends = res.value.sumOf { it.value.toDoubleOrNull() ?: 0.0 }.toFloat()
                    val sumSpends = SumSpendsOfMonth(
                        res.value.firstOrNull()?.date ?: dateFormatFull.format(currentDate),
                        totalFriendSpends
                    )
                    _friendSpendsLiveData.postValue(sumSpends)

                    val friendSpendList = res.value.groupBy { it.spendName }.map { (category, list) ->
                        val totalValue = list.sumOf { it.value.toDoubleOrNull() ?: 0.0 }
                        Spend(0, category, totalValue.toString(), list.firstOrNull()?.date ?: "", list.firstOrNull()?.cardId ?: "", list.firstOrNull()?.url)
                    }

                    // СОХРАНЯЕМ В ЛОКАЛЬНУЮ ПЕРЕМЕННУЮ
                    localFriendSpendsList = friendSpendList
                    // ДАННЫЕ ДРУГА ПРИШЛИ! Ставим флаг и проверяем барьер
                    isFriendSpendsLoaded = true
                    checkAndMerge()
                }
                is Result.Error -> {
                    // Даже если ошибка, снимаем блокировку, чтобы показать хотя бы твои траты
                    isFriendSpendsLoaded = true
                    checkAndMerge()
                    Log.e("StatisticViewModel", "Error Friend Expenses: ${res.error.message}")
                }
            }
        }
    }

   // Возвращаем метод для Фрагмента, чтобы он мог узнать текущий месяц
    fun getDateDbFormat(): String {
        return dateFormatMonthYear.format(currentDate)
    }

    // 3. Баланс друга
    fun getFriendBalance() {
        getBalanceFriendUseCase(MainPrefs.mailFriend).onEach { resource ->
            if (resource is Resource.Success) {
                _friendsBalanceLiveData.value = resource.data
            }
        }.launchIn(viewModelScope)
    }

    // 4. Твой баланс
    fun getBalance() {
        getBalanceUserUseCase().onEach { resource ->
            if (resource is Resource.Success) {
                _userBalanceLiveData.value = resource.data
            }
        }.launchIn(viewModelScope)
    }

    // 5. Твои расходы (Детальный список)
    private fun getMonthlyUserSpends() {
        getMonthlySpendsUseCase(dateMonth = dateFormatMonthYear.format(currentDate)).onEach { resource ->
            if (resource is Resource.Success) {
                // 1. Сохраняем твои детальные траты во внутреннюю переменную
                localUserSpendsList = resource.data ?: emptyList()

                // 2. Говорим: "Мои данные готовы!" и дергаем барьер
                isUserSpendsLoaded = true
                checkAndMerge()
            }
        }.launchIn(viewModelScope)
    }
    @Synchronized
    private fun checkAndMerge() {
        // Ждем, пока загрузятся ОБА списка
        if (isUserSpendsLoaded && isFriendSpendsLoaded) {
            // Склеиваем и сортируем
            val mergedList = mergeSpends(localUserSpendsList, localFriendSpendsList)

            // Отправляем готовый красивый список в UI
            _usersSpendsListLiveData.postValue(mergedList)

            // Выключаем прогресс-бар
            _stateLiveData.postValue(State.SUCCESS)
        }
    }



    /**
     *  слияние двух списков расходов (Твои и Друга)
     *  никаких циклов в цикле!
     */
    private fun mergeSpends(userList: List<Spend>?, friendList: List<Spend>?): List<PairSpends> {
        val safeUserList = userList ?: emptyList()
        val safeFriendList = friendList ?: emptyList()

        // 1. Собираем все уникальные категории из обоих списков (Set исключает дубликаты)
        val allCategories = (safeUserList.map { it.spendName } + safeFriendList.map { it.spendName }).toSet()

        // 2. Проходимся по каждой категории и склеиваем данные
        return allCategories.map { category ->
            val userSpend = safeUserList.find { it.spendName == category }
            val friendSpend = safeFriendList.find { it.spendName == category }

            val userValue = userSpend?.value?.toFloatOrNull() ?: 0f
            val friendValue = friendSpend?.value?.toFloatOrNull() ?: 0f
            val iconUrl = userSpend?.url ?: friendSpend?.url

            PairSpends(
                valueUser = userValue,
                valueFriend = friendValue,
                nameSpend = category,
                url = iconUrl
            )
        }.sortedByDescending { it.valueUser + it.valueFriend } // Сортируем: сверху самые большие общие траты
    }
}