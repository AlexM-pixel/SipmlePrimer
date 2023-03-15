package com.example.mysympleapplication.hw9.newDesign.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetCategoriesUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.UpdateCategoryPayUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ImagesViewModel @Inject constructor(                  // viewModel для сохранения категории с новым названием картинки
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val updateUseCase: UpdateCategoryPayUseCase
) : ViewModel() {

    private var listCategories: List<NameSpend>? = mutableListOf()
    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData

    fun getCategoryToUpdate(nameImage: String, ruName: String) {
        getCategoriesUseCase().onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    listCategories = it.data
                    val nameSpend = getCategoryByImageName(nameImage, ruName = ruName)
                    if (nameSpend != null) {
                        updateNameSpend(nameSpend = nameSpend)
                    } else {
                        _stateLiveData.value = State.ERROR
                        Log.e("nameSpend == null", "$nameSpend")
                    }
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("ImagesViewModel", "Error: ${it.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getCategoryByImageName(nameImage: String, ruName: String): NameSpend? {
        listCategories?.forEach {
            if (it.ruName.equals(ruName)) {
                it.imageName = nameImage
                return it
            }
        }
        return null
    }

    private fun updateNameSpend(nameSpend: NameSpend) {
        updateUseCase(nameSpend = nameSpend).onEach {
            when (it) {
                is Resource.Loading -> {
                    _stateLiveData.value = State.LOADING
                }
                is Resource.Success -> {
                    _stateLiveData.value = State.SUCCESS
                }
                is Resource.Error -> {
                    _stateLiveData.value = State.ERROR
                    Log.e("updateNameSpend", "Error: ${it.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

}