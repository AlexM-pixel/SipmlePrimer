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
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.DownloadImageUrlUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetModelsSpendsUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetMonthlySpendsUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class MonthlySpendsViewModel @Inject constructor(
    private val repoFrStorage: FirestorageRepository,
    private val getMonthlySpendsUseCase: GetMonthlySpendsUseCase,
    private val getCategoryPay: GetModelsSpendsUseCase
) : ViewModel() {
    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData
    private val _monthlySpendsLiveData = MutableLiveData<List<Spend>>()
    val monthlySpendsLiveData: MutableLiveData<List<Spend>> get() = _monthlySpendsLiveData


    fun getMonthlySpends(month: String) {
     //   var listCategory:List<NameSpend>
        getMonthlySpendsUseCase(dateMonth = month).onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    _stateLiveData.value = State.SUCCESS
                    _monthlySpendsLiveData.value = it.data ?: mutableListOf()
                    //listCategory = getCategoryPay.getModelsSpends()
                  //  getUrlImages(it.data!!, listCategory)
                    Log.e("getMonthlySpendsUseCase", "spendsSuccess")
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("homeViewModel", "Error: ${it.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    suspend fun getUrlImages(
        listSpend: List<Spend>, listCategory: List<NameSpend>
    ) {
        Log.e("getUrlImages", "listSize = ${listCategory.size}")
        for (category in listCategory) {
            for (spend in listSpend) {
                if (spend.spendName.equals(category.ruName)) {
                    val uri = repoFrStorage.downloadImageUrl(category.imageName)
                    Log.e("repoFrStorage.downleUrl", "uri = $uri")
                    spend.url = uri.toString()
                    listSpend[listSpend.indexOf(spend)].url.toString()
                }
            }
        }
        _monthlySpendsLiveData.value = listSpend
    }
}



