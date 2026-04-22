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

    // =========================================================================
    // 1. СОСТОЯНИЯ ЭКРАНА (LIVE DATA ДЛЯ ФРАГМЕНТА)
    // =========================================================================

    // Индикатор загрузки (крутилка)
    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData

    // Общая сумма твоих расходов за месяц
    private val _userSpendsLiveData = MutableLiveData<SumSpendsOfMonth?>()
    val userSpendsLiveData: LiveData<SumSpendsOfMonth?> get() = _userSpendsLiveData

    // Общая сумма расходов друга за месяц
    private val _friendSpendsLiveData = MutableLiveData<SumSpendsOfMonth?>()
    val friendSpendsLiveData: LiveData<SumSpendsOfMonth?> get() = _friendSpendsLiveData

    // Балансы на картах
    private val _friendsBalanceLiveData = MutableLiveData<Balance?>()
    val friendsBalanceLiveData: LiveData<Balance?> get() = _friendsBalanceLiveData

    private val _userBalanceLiveData = MutableLiveData<Balance?>()
    val userBalanceLiveData: LiveData<Balance?> get() = _userBalanceLiveData

    // Итоговый склеенный список трат (Твои + Друга) по категориям
    private val _pairSpendsLiveData = MutableLiveData<List<PairSpends>>()
    val pairSpendsLiveData: LiveData<List<PairSpends>> get() = _pairSpendsLiveData


    // =========================================================================
    // 2. ВНУТРЕННИЕ ПЕРЕМЕННЫЕ (ДЛЯ ВЫЧИСЛЕНИЙ)
    // =========================================================================

    // Эти списки мы не отдаем во фрагмент. Мы храним их здесь, чтобы
    // мгновенно склеить их вместе, когда придут оба ответа.
    private var localUserSpendsList: List<Spend> = emptyList()
    private var localFriendSpendsList: List<Spend> = emptyList()

    // Внутренние суммы для графика
    private var localUserTotal: SumSpendsOfMonth? = null
    private var localFriendTotal: SumSpendsOfMonth? = null

    // Флаги "Барьера". Они говорят нам, пришли ли данные из баз.
    private var isUserListLoaded = false
    private var isUserTotalLoaded = false
    private var isFriendDataLoaded = false // Firebase отдает и список, и сумму разом

    // Форматтеры дат (Создаем один раз, чтобы не засорять память)
    private val currentDate = Date()
    private val dateFormatFull = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateFormatMonthYear = SimpleDateFormat("MM-yyyy", Locale.getDefault())
    private val dateFormatFirestore = SimpleDateFormat("yyyy-MM", Locale.getDefault())


    init {
        loadAllData()
    }

    // Стартовая загрузка всех данных
    fun loadAllData() {
        _stateLiveData.value = State.LOADING

        // Сбрасываем барьер перед новой загрузкой


        isUserListLoaded = false
        isUserTotalLoaded = false
        isFriendDataLoaded = false

        getUserExpenses()        // Запрос общей суммы (Room)
        getMonthlyUserSpends()   // Запрос детального списка (Room)
        getBalance()             // Запрос баланса (Room)

        getFriendsExpenses()     // Запрос списка и суммы друга (Firebase)
        getFriendBalance()       // Запрос баланса друга (Firebase)
    }

    // =========================================================================
    // 3. ПОЛУЧЕНИЕ ДАННЫХ
    // =========================================================================

    // А) Твои расходы (Общая сумма из Room)
    fun getUserExpenses() {
        getExpensesUserUseCase(dateFormatFull.format(currentDate)).onEach { resource ->
            when (resource) {
                is Resource.Success -> {
                    localUserTotal = resource.data
                    isUserTotalLoaded = true
                    checkAndMerge()
                }

                is Resource.Error -> {
                    localUserTotal = SumSpendsOfMonth(dateFormatFull.format(currentDate), 0f)
                    isUserTotalLoaded = true
                    checkAndMerge()
                    Log.e("StatisticViewModel", "Error User Expenses: ${resource.message}")
                }

                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    // Б) Твои расходы (Детальный список из Room)
    private fun getMonthlyUserSpends() {
        getMonthlySpendsUseCase(dateMonth = dateFormatMonthYear.format(currentDate)).onEach { resource ->
            if (resource is Resource.Success) {
                // 1. Сохраняем детальные траты во внутреннюю переменную
                localUserSpendsList = resource.data ?: emptyList()
                // 2. Говорим: "Мои данные готовы!" и дергаем барьер
                isUserListLoaded = true
                checkAndMerge()
            }
        }.launchIn(viewModelScope)
    }

    // В) Расходы друга (Детальный список и Общая сумма из Firebase)
    fun getFriendsExpenses() {
        viewModelScope.launch(Dispatchers.IO) {
            val friendEmail = MainPrefs.mailFriend

            // Если друга нет, пропускаем загрузку из сети
            if (friendEmail.isEmpty()) {
                isFriendDataLoaded = true
                checkAndMerge()
                return@launch
            }

            when (val res =
                getExpensesFriendUseCase(dateFormatFirestore.format(currentDate), friendEmail)) {
                is Result.Value -> {
                    // 1. Считаем общую сумму друга и отправляем во фрагмент
                    val totalFriendSpends =
                        res.value.sumOf { it.value.toDoubleOrNull() ?: 0.0 }.toFloat()
                    val sumSpends = SumSpendsOfMonth(
                        res.value.firstOrNull()?.date ?: dateFormatFull.format(currentDate),
                        totalFriendSpends
                    )
                    // СОХРАНЯЕМ СУММУ ЛОКАЛЬНО
                    localFriendTotal = sumSpends

                    // 2. Группируем детальные траты друга по названиям мест
                    localFriendSpendsList =
                        res.value.groupBy { it.spendName }.map { (category, list) ->
                            val totalValue = list.sumOf { it.value.toDoubleOrNull() ?: 0.0 }
                            Spend(
                                0,
                                category,
                                totalValue.toString(),
                                list.firstOrNull()?.date ?: "",
                                list.firstOrNull()?.cardId ?: "",
                                list.firstOrNull()?.url
                            )
                        }

                    // 3. "Данные друга готовы!"  дергаем барьер
                    isFriendDataLoaded = true
                    checkAndMerge()
                }

                is Result.Error -> {
                    // Если ошибка интернета - снимаем барьер, чтобы показать хотя бы данные юзера
                    isFriendDataLoaded = true
                    checkAndMerge()
                    Log.e("StatisticViewModel", "Error Friend Expenses: ${res.error.message}")
                }
            }
        }
    }

    // Г) Балансы (Room и Firebase)
    fun getFriendBalance() {
        getBalanceFriendUseCase(MainPrefs.mailFriend).onEach { resource ->
            if (resource is Resource.Success) _friendsBalanceLiveData.value = resource.data
        }.launchIn(viewModelScope)
    }

    fun getBalance() {
        getBalanceUserUseCase().onEach { resource ->
            if (resource is Resource.Success) _userBalanceLiveData.postValue(resource.data)
        }.launchIn(viewModelScope)
    }

    // Возвращаем текущий месяц для UI (например, "Апрель")
    fun getDateDbFormat(): String {
        return dateFormatMonthYear.format(currentDate)
    }

    // =========================================================================
    // 4. ЛОГИКА СЛИЯНИЯ СПИСКОВ (БАРЬЕР И МАГИЯ KOTLIN)
    // =========================================================================

    /**
     * Барьер. Этот метод вызывается дважды (когда загрузился ты, и когда загрузился друг).
     * Он ждет, пока оба флага станут true, и только тогда склеивает списки.
     * @Synchronized защищает от одновременного доступа с разных потоков.
     */
    @Synchronized
    private fun checkAndMerge() {
        if (isUserListLoaded && isUserTotalLoaded && isFriendDataLoaded) {
            // 1. Данные пришли из обеих баз. Запускаем слияние!
            val mergedList = mergeSpends(localUserSpendsList, localFriendSpendsList)
            // 2. Отправляем готовый список во фрагмент (наконец-то!)
            _pairSpendsLiveData.postValue(mergedList)
            // 1. Отдаем суммы для Пончика
            _userSpendsLiveData.postValue(localUserTotal)
            _friendSpendsLiveData.postValue(localFriendTotal)

            // 3. Прячем крутилку загрузки
            _stateLiveData.postValue(State.SUCCESS)
        }
    }

    /**
     * Метод склеивания двух списков в один красивый список PairSpends
     */
    private fun mergeSpends(userList: List<Spend>, friendList: List<Spend>): List<PairSpends> {
        // Собираем уникальные категории (например, "Евроопт", "Аптека") из обоих списков
        val allCategories =
            (userList.map { it.spendName } + friendList.map { it.spendName }).toSet()

        return allCategories.map { category ->
            // Находим сумму в этой категории для тебя
            val userValue = userList.find { it.spendName == category }?.value?.toFloatOrNull() ?: 0f
            // Находим сумму в этой категории для друга
            val friendValue =
                friendList.find { it.spendName == category }?.value?.toFloatOrNull() ?: 0f

            // Ищем иконку категории
            val iconUrl = userList.find { it.spendName == category }?.url
                ?: friendList.find { it.spendName == category }?.url

            PairSpends(
                valueUser = userValue,
                valueFriend = friendValue,
                nameSpend = category,
                url = iconUrl
            )
        }
            .sortedByDescending { it.valueUser + it.valueFriend } // Сортируем: наверху самые дорогие общие покупки
    }
}