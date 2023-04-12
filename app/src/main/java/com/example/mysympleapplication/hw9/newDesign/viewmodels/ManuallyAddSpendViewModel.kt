package com.example.mysympleapplication.hw9.newDesign.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.newDesign.domain.model.Balance
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.*
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class ManuallyAddSpendViewModel @Inject constructor(
    private val getCategoryUseCase: GetCategoriesUseCase,
    private val saveSpendDbUseCase: SaveSpendDbUseCase,
    private val saveSpendFrStoreUseCase: SaveSpendFrStoreUseCase,
    private val addNewCategoryUseCase: InsertModelNameBySpendUseCase,
    private val saveBalanceUseCase: SaveBalanceUseCase
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData
    private val _namesCategorySpendLiveData = MutableLiveData<List<String>>()
    val namesCategorySpendLiveData: MutableLiveData<List<String>> get() = _namesCategorySpendLiveData

    fun getAllNamesSpendForAutoComplete() {
        getCategoryUseCase().onEach { listNamesSpend ->
            when (listNamesSpend) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    Log.e("колличество категории", "count: ${listNamesSpend.data?.size}")
                    namesCategorySpendLiveData.value = listNamesSpend.data?.map { it.ruName }
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("getCategoryToImageM", "ErrorM: ${listNamesSpend.message}")
                }
            }
        }.launchIn(viewModelScope)
    }


    fun addNewSpend(name: String, value: String, date: String, nameImage: String?) {
        if (!isHavingNewCategory(name)) {
            addNewCategory(name)       // усли имя спенды новое, создаю новую категорию
        }
        val _id = getId(name, value, Date())
        val _date = getDate(date)
        Log.e("saveSpendM!!!", _date)
        val spend =
            Spend(
                id = _id,
                spendName = name,
                value = value,
                date = _date,
                url = nameImage
            )
        saveSpendDbUseCase(spend = spend).onEach {
            Log.e("saveSpendM", "onEachDbM")
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    Log.e("saveSpendM", "saveSpendDbUseCaseM Success!")
                    _stateLiveData.value = State.SUCCESS
                }
                is Resource.Error -> {
                    Log.e("saveSpendM", "saveSpendDbUseCaseM ERROR: i${it.message}")
                }
            }
        }.launchIn(viewModelScope)
        saveSpendFrStoreUseCase(spend = spend).onEach {
            Log.e("saveSpendM", "onEachFrStoreM")
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    Log.e("saveSpendM", "saveSpendFrStoreUseCaseM Success!")
                    //  _stateLiveData.value = State.SUCCESS
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("saveSpendM", "saveSpendFrStoreUseCaseM ERROR: i${it.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun isHavingNewCategory(name: String): Boolean {
        for (ruNameCategory in _namesCategorySpendLiveData.value!!) {
            if (name.equals(ruNameCategory)) {
                Log.e("CheckCategory", "Category we have category!!! : $name")
                return true
            }
        }
        return false
    }

    private fun addNewCategory(nameNewCategory: String) {
        Log.e("CheckCategory", "Just don't have category : $nameNewCategory")
        val nameSpend = NameSpend(
            id = null, nameSpend = nameNewCategory, ruName = nameNewCategory,
            Config.DEF_SPEND_NAME
        )
        viewModelScope.launch(Dispatchers.IO) {
            addNewCategoryUseCase.addNewModelNameBySpend(nameSpend)
        }
    }

    fun changeBalance(newBalance: Float) {
        Log.e("changeBalance!!!", "Just saving new Balance! : $newBalance ")
        val balance = Balance(0, newBalance.toString())
        viewModelScope.launch(Dispatchers.IO) {
            saveBalanceUseCase.saveBalance(
                MainPrefs.mailUser,
                balance = balance
            )
        }
    }


    private fun getId(name: String, value: String, date: Date): Long {
        var hash = 3L
        hash = 31 * hash + date.hashCode()
        hash = 31 * hash + value.hashCode()
        hash = 31 * hash + name.hashCode()
        Log.e("MAddSpendViewModel", "Hash for date:  $date")
        return hash
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDate(date: String): String {
        val oldDatePattern = "MMM d, yyyy"
        val newDatePattern = "yyyy-MM-dd"
        val oldFormatter = DateTimeFormatter.ofPattern(oldDatePattern)
        val newFormatter = DateTimeFormatter.ofPattern(newDatePattern)
        return LocalDate.parse(date, oldFormatter).format(newFormatter)
    }

}