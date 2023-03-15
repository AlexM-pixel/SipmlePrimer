package com.example.mysympleapplication.hw9.newDesign.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.newDesign.domain.model.DetailsSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.DeleteSpendUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetCategoriesUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetDetailsUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetSpendsByNameUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailMonthlyViewModel @Inject constructor(
    private val spendsByNameUseCase: GetSpendsByNameUseCase,
    private val deleteSpendUseCase: DeleteSpendUseCase,
    private val getDetailsUseCase: GetDetailsUseCase,
    private val getCategoryUseCase: GetCategoriesUseCase
) :
    ViewModel() {
    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData
    private val _detailSpendsByNameLiveData = MutableLiveData<List<Spend>>()
    val detailSpendsByNameLiveData: MutableLiveData<List<Spend>> get() = _detailSpendsByNameLiveData
    private val _detailsLiveData = MutableLiveData<List<DetailsSpend>>()
    val detailLiveData: MutableLiveData<List<DetailsSpend>> get() = _detailsLiveData
    val imageNameLiveData = MutableLiveData<String>()


    fun getMonthlySpends(name: String, month: String) {
        viewModelScope.launch {
            spendsByNameUseCase.getListSpendsByName(name, dateMonth = month).collect {
                _detailSpendsByNameLiveData.value = it
                getCurSpendDetailsList()
            }
        }
    }


    fun deleteSpend(id: String, name: String, month: String) {
        Log.e("deleteSpend", "This is deleteFun name= $name , month= $month")
        deleteSpendUseCase(id, mail = MainPrefs.mailUser).onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    _stateLiveData.value = State.SUCCESS
                    Log.e("deleteSpend", "Delete Spend Success")
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("deleteSpend", "Error: ${it.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getCurSpendDetailsList() {
        getDetailsUseCase().onEach {
            when (it) {
                is Resource.Success -> {
                    detailLiveData.value = it.data
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getCategoryToImage(name: String) {
        getCategoryUseCase().onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    _stateLiveData.value = State.SUCCESS
                    imageNameLiveData.value = getNameImage(name, it.data!!)
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("getCategoryToImage", "Error: ${it.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getNameImage(spendName: String, listCategory: List<NameSpend>): String {
        for (category in listCategory) {
            if (spendName.equals(category.ruName)) {
                Log.e(
                    "getNameImage",
                    "categoryPay=${category.ruName}, imageName=${category.imageName}"
                )
                return category.imageName
            }
        }
        return Config.DEF_SPEND_NAME
    }
}