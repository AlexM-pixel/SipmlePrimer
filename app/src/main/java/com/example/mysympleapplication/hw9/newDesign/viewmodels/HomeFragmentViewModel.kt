package com.example.mysympleapplication.hw9.newDesign.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.data.mapper.MonthUiMapper
import com.example.mysympleapplication.hw9.newDesign.domain.model.BankCard
import com.example.mysympleapplication.hw9.newDesign.domain.model.MonthUiModel
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.DeleteBankCardUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetBankCardsUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetMonthlyExpensesUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.SaveBankCardUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.UpdateCardUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeFragmentViewModel @Inject constructor(
    private val useCaseGetMonthlyExpenses: GetMonthlyExpensesUseCase,
    private val getBankCardsUseCase: GetBankCardsUseCase,
    private val deleteBankCardUseCase: DeleteBankCardUseCase,
    private val editBankCardUseCase: UpdateCardUseCase,
    private val saveBankCardUseCase: SaveBankCardUseCase,
    private var uiMapper: MonthUiMapper
) :
    ViewModel() {
    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData


    private val _uiState = MutableLiveData<List<MonthUiModel>>()
    val uiState: LiveData<List<MonthUiModel>> get() = _uiState

    // ИЗМЕНИЛИСЬ ДАННЫЕ: Теперь список карт
    private val _cardsLiveData = MutableLiveData<List<BankCard>>()
    val cardsLiveData: LiveData<List<BankCard>> get() = _cardsLiveData

    fun getMonthlyExpenses() {
        useCaseGetMonthlyExpenses().onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }

                is Resource.Success -> {
                    _stateLiveData.value = State.SUCCESS
                    val rawData = resource.data ?: emptyList()

                    // !!! ПРЕВРАЩАЕМ ДАННЫЕ В UI-МОДЕЛИ !!!
                    val uiModels = uiMapper.map(rawData)
                    _uiState.value = uiModels
                }

                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("homeViewModel", "Error: ${resource.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    // НОВЫЙ МЕТОД ДЛЯ ПОЛУЧЕНИЯ КАРТ
    fun getCards() {
        getBankCardsUseCase().onEach {
            when (it) {
                is Resource.Loading -> _stateLiveData.value = State.LOADING
                is Resource.Success -> {
                    _stateLiveData.value = State.SUCCESS
                    // Обновляем список карт
                    _cardsLiveData.postValue(it.data ?: emptyList())
                }

                is Resource.Error -> _stateLiveData.value = State.ERROR
            }
        }.launchIn(viewModelScope)
    }

    // Удаление карты
    fun deleteCard(cardId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteBankCardUseCase(cardId)
            // Список карт обновится автоматически через Flow
        }
    }

    // Редактирование карты (имя, цифры, баланс)
    fun editCard(card: BankCard) {
        viewModelScope.launch(Dispatchers.IO) {
            editBankCardUseCase(card)
        }
    }

    fun saveCard(card: BankCard) {
        viewModelScope.launch(Dispatchers.IO) {
            saveBankCardUseCase(card) // Тот самый UseCase, что мы писали ранее
        }
    }

}