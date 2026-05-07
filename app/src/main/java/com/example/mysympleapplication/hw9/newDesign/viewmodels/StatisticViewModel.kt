package com.example.mysympleapplication.hw9.newDesign.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.domain.model.PairSpendUiModel
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.domain.model.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.domain.model.UserDocuments
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.*
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import com.google.firebase.firestore.FirebaseFirestore
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
    private val getMonthlySpendsUseCase: GetMonthlySpendsUseCase,
    private val getFriendProfileUseCase: GetFriendProfileUseCase
) : ViewModel() {

    // =========================================================================
    // 1. СОСТОЯНИЯ ЭКРАНА (LIVE DATA ДЛЯ ФРАГМЕНТА)
    // =========================================================================

    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData

    private val _userSpendsLiveData = MutableLiveData<SumSpendsOfMonth?>()
    val userSpendsLiveData: LiveData<SumSpendsOfMonth?> get() = _userSpendsLiveData

    private val _friendSpendsLiveData = MutableLiveData<SumSpendsOfMonth?>()
    val friendSpendsLiveData: LiveData<SumSpendsOfMonth?> get() = _friendSpendsLiveData

    private val _friendsBalanceLiveData = MutableLiveData<Balance?>()
    val friendsBalanceLiveData: LiveData<Balance?> get() = _friendsBalanceLiveData

    private val _userBalanceLiveData = MutableLiveData<Balance?>()
    val userBalanceLiveData: LiveData<Balance?> get() = _userBalanceLiveData

    // LiveData для имени друга (для шапки и графика)
    private val _friendNameLiveData = MutableLiveData<String>()
    val friendNameLiveData: LiveData<String> get() = _friendNameLiveData
    private val _friendAvatarLiveData = MutableLiveData<String>()
    val friendAvatarLiveData: LiveData<String> get() = _friendAvatarLiveData

    // ГОТОВЫЙ СПИСОК ДЛЯ UI (Адаптера)
    private val _uiSpendsListLiveData = MutableLiveData<List<PairSpendUiModel>>()
    val uiSpendsListLiveData: LiveData<List<PairSpendUiModel>> get() = _uiSpendsListLiveData

    // =========================================================================
    // 2. ВНУТРЕННИЕ ПЕРЕМЕННЫЕ (ДЛЯ ВЫЧИСЛЕНИЙ)
    // =========================================================================

    private var localUserSpendsList: List<Spend> = emptyList()
    private var localFriendSpendsList: List<Spend> = emptyList()
    private var localUserTotal: SumSpendsOfMonth? = null
    private var localFriendTotal: SumSpendsOfMonth? = null

    // Данные профиля друга для UI-модели
    private var localFriendName: String = "Друг"
    private var localFriendAvatar: String = "place_holder_av"

    // Флаги "Барьера"
    private var isUserListLoaded = false
    private var isUserTotalLoaded = false
    private var isFriendDataLoaded = false // Firebase отдает и список, и сумму разом
    private var isFriendProfileLoaded = false

    // Форматтеры
    private val currentDate = Date()
    private val dateFormatFull = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateFormatMonthYear = SimpleDateFormat("MM-yyyy", Locale.getDefault())
    private val dateFormatFirestore = SimpleDateFormat("yyyy-MM", Locale.getDefault())

    init {
        loadAllData()
    }

    fun loadAllData() {
        _stateLiveData.value = State.LOADING

        // Сбрасываем барьер перед новой загрузкой
        isUserListLoaded = false
        isUserTotalLoaded = false
        isFriendDataLoaded = false
        isFriendProfileLoaded = false

        // Запускаем асинхронные запросы
        getUserExpenses()
        getMonthlyUserSpends()
        getBalance()
        getFriendBalance()
        getFriendsExpenses()
        getFriendProfileInfo()   // Получаем имя и аватарку для UI-моделей
    }

    // =========================================================================
    // 3. ПОЛУЧЕНИЕ ДАННЫХ
    // =========================================================================

    // А) Твои расходы (Общая сумма)
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
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    // Б) Твои расходы (Детальный список)
    private fun getMonthlyUserSpends() {
        getMonthlySpendsUseCase(dateMonth = dateFormatMonthYear.format(currentDate)).onEach { resource ->
            if (resource is Resource.Success) {
                localUserSpendsList = resource.data ?: emptyList()
                isUserListLoaded = true
                checkAndMerge()
            }
        }.launchIn(viewModelScope)
    }

    // В) Расходы друга (Детальный список и Общая сумма из Firebase)
    fun getFriendsExpenses() {
        viewModelScope.launch(Dispatchers.IO) {
            val friendEmail = MainPrefs.mailFriend

            if (friendEmail.isEmpty()) {
                isFriendDataLoaded = true
                checkAndMerge()
                return@launch
            }

            when (val res = getExpensesFriendUseCase(dateFormatFirestore.format(currentDate), friendEmail)) {
                is Result.Value -> {
                    // Сумма
                    val totalFriendSpends = res.value.sumOf { it.value.toDoubleOrNull() ?: 0.0 }.toFloat()
                    localFriendTotal = SumSpendsOfMonth(
                        res.value.firstOrNull()?.date ?: dateFormatFull.format(currentDate),
                        totalFriendSpends
                    )

                    // Детальный список
                    localFriendSpendsList = res.value.groupBy { it.spendName }.map { (category, list) ->
                        val totalValue = list.sumOf { it.value.toDoubleOrNull() ?: 0.0 }
                        Spend(0, category, totalValue.toString(), list.firstOrNull()?.date ?: "", list.firstOrNull()?.cardId ?: "", list.firstOrNull()?.url)
                    }

                    isFriendDataLoaded = true
                    checkAndMerge()
                }
                is Result.Error -> {
                    isFriendDataLoaded = true
                    checkAndMerge()
                }
            }
        }
    }

    // Г) Балансы
    fun getFriendBalance() {
        getBalanceFriendUseCase(MainPrefs.mailFriend).onEach { resource ->
            if (resource is Resource.Success) _friendsBalanceLiveData.postValue(resource.data)
        }.launchIn(viewModelScope)
    }

    fun getBalance() {
        getBalanceUserUseCase().onEach { resource ->
            if (resource is Resource.Success) _userBalanceLiveData.postValue(resource.data)
        }.launchIn(viewModelScope)
    }

    // Д) Профиль друга (Аватарка и Имя)
    private fun getFriendProfileInfo() {
        val friendEmail = MainPrefs.mailFriend
        if (friendEmail.isEmpty()) {
            isFriendProfileLoaded = true
            checkAndMerge()
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            when (val result = getFriendProfileUseCase(friendEmail)) {
                is Result.Value -> {
                    // result.value - это наша пара Pair(name, avatar)
                    val name = result.value.first
                    val avatarName = result.value.second

                    _friendNameLiveData.postValue(name)
                    _friendAvatarLiveData.postValue(avatarName)

                    localFriendName = name
                    localFriendAvatar = avatarName
                }
                is Result.Error -> {
                    Log.e("StatisticViewModel", "Не удалось загрузить профиль: ${result.error}")
                }
            }

            // В любом случае (успех или ошибка) дергаем барьер
            isFriendProfileLoaded = true
            checkAndMerge()
        }
    }

    fun getDateDbFormat(): String {
        return dateFormatMonthYear.format(currentDate)
    }

    // =========================================================================
    // 4. ЛОГИКА СЛИЯНИЯ СПИСКОВ (БАРЬЕР И МАППИНГ)
    // =========================================================================

    @Synchronized
    private fun checkAndMerge() {
        // Ждем все 4 потока данных
        if (isUserListLoaded && isUserTotalLoaded && isFriendDataLoaded && isFriendProfileLoaded) {

            // 1. Отдаем суммы для Пончика
            _userSpendsLiveData.postValue(localUserTotal)
            _friendSpendsLiveData.postValue(localFriendTotal)

            // 2. Склеиваем и создаем готовые UI Модели
            val uiModels = createUiModels(localUserSpendsList, localFriendSpendsList)

            // 3. Отправляем в адаптер
            _uiSpendsListLiveData.postValue(uiModels)

            // 4. Выключаем крутилку загрузки
            _stateLiveData.postValue(State.SUCCESS)
        }
    }

    /**
     * Создает готовый список для отображения в Адаптере.
     * Здесь мы берем сырые траты и склеиваем их с аватарками и именами.
     */
    private fun createUiModels(userList: List<Spend>, friendList: List<Spend>): List<PairSpendUiModel> {
        val myEmailStr = MainPrefs.mailUser
        val myName = MainPrefs.userName.ifEmpty { myEmailStr.substringBefore("@").replaceFirstChar { it.uppercase() } }
        val myAvatar = MainPrefs.userAvatarName

        val allCategories = (userList.map { it.spendName } + friendList.map { it.spendName }).toSet()

        return allCategories.map { category ->
            val userSpend = userList.find { it.spendName == category }
            val friendSpend = friendList.find { it.spendName == category }

            val userValue = userSpend?.value?.toFloatOrNull() ?: 0f
            val friendValue = friendSpend?.value?.toFloatOrNull() ?: 0f

            // Ищем иконку (берем у того, кто совершил покупку первым)
            val iconUrl = userSpend?.url ?: friendSpend?.url ?: "produkti"

            PairSpendUiModel(
                categoryName = category,
                categoryIconUrl = iconUrl,
                myName = myName,
                myAvatar = myAvatar,
                myAmount = userValue,
                friendName = localFriendName,
                friendAvatar = localFriendAvatar,
                friendAmount = friendValue,
                totalAmount = userValue + friendValue
            )
        }.sortedByDescending { it.totalAmount } // Сортируем: наверху самые дорогие общие покупки
    }
}