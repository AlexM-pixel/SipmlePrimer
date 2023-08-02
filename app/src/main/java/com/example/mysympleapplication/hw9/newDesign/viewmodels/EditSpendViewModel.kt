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
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.*
import com.example.mysympleapplication.hw9.newDesign.utils.Config.DEF_SPEND_NAME
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditSpendViewModel @Inject constructor(
    private val insertDetails: InsertDetailsSpendUseCase,
    private val getDetailsUseCase: GetDetailsFlowUseCase,
    private val getCategoryUseCase: GetCategoriesUseCase,
    private val getSpendByIdUseCase: GetSpendByIdUseCase,
    private val delDetailsSpendUseCase: DelDetailsSpendUseCase,
    private val updateSpendDbCase: UpdateSpendDbUseCase,
    private val saveSpendFrStoreUseCase: SaveSpendFrStoreUseCase,

    ) :
    ViewModel() {
    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData

    private val _detailsLiveData = MutableLiveData<List<DetailsSpend>>()
    val detailsLiveData: LiveData<List<DetailsSpend>> get() = _detailsLiveData

    private val _spendLiveData = MutableLiveData<Spend?>()
    val spendLiveData: LiveData<Spend?> get() = _spendLiveData

    val imageNameLiveData = MutableLiveData<String>()
    val errorLiveData = MutableLiveData<String>()


    fun saveDetailsSpend(name: String, value: String, fKey: Long) {
        val _id = System.currentTimeMillis()
        val details: DetailsSpend = DetailsSpend(id = _id, name = name, value = value, fKey = fKey)
        insertDetails(details, MainPrefs.mailUser).onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    _stateLiveData.value = State.SUCCESS

                    Log.e("saveDetailsSpend", "detailsSuccess")
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    errorLiveData.value = it.message.toString()
                    Log.e("EditSpendViewModel", "Error: ${it.message}")
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
        return DEF_SPEND_NAME
    }

    fun getDetailsList(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            getDetailsUseCase.getList(id).collect {
                _detailsLiveData.postValue(it)
            }
        }
    }

    fun getSpend(id: String) {
        getSpendByIdUseCase(id).onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    _stateLiveData.value = State.SUCCESS
                    _spendLiveData.value = it.data
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("getCategoryToImage", "Error: ${it.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deleteDetails(id: Long) {
        Log.e("deleteDetails", "This is deleteDetailsBYSpend")
        delDetailsSpendUseCase(id = id, mail = MainPrefs.mailUser).onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    _stateLiveData.value = State.SUCCESS
                    Log.e("deleteDetails", "Delete Details Success")
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("deleteDetails", "Error: ${it.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateEditSpend(spend: Spend) {
        updateSpendDbCase(spend=spend).onEach {
            Log.e("updateSpend", "onUpdateEachDb")
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    Log.e("UpdateSpendM", "updateSpendDbUseCase Success!")
                    _stateLiveData.value = State.SUCCESS
                }
                is Resource.Error -> {
                    Log.e("updateSpendM", "updateSpendDbUseCase ERROR: i${it.message}")
                }
            }
        }.launchIn(viewModelScope)

        saveSpendFrStoreUseCase(spend = spend).onEach {
            Log.e("updateSpendM", "onUpdateEachFrStoreM")
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    Log.e("updateSpendM", "updateSpendFrStoreUseCaseM Success!")
                    //  _stateLiveData.value = State.SUCCESS
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("updateSpendM", "updateFrStoreUseCaseM ERROR: i${it.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

}


