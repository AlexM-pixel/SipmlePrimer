package com.example.mysympleapplication.hw9.newDesign.viewmodels


import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.LogOutUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs.setBankNames
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsFragmentViewModel @Inject constructor(
    private val useCaseLogOut: LogOutUseCase
) : ViewModel() {
    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData
    private val _liveDataResult = MutableLiveData<String>()
    val liveDataResult: LiveData<String> get() = _liveDataResult

    fun setNewBankName(newNameBank: String) {
        if (!TextUtils.isEmpty(newNameBank)) {
            try {
                setBankNames.add(newNameBank)
                _liveDataResult.value = "Имя : $newNameBank Сохранено!"
                Log.e("SettingsViewModel","set.size=${setBankNames.size}, lastNameSet= ${setBankNames.lastOrNull()}")
            } catch (e: Exception) {
                _liveDataResult.value = e.message
            }

        }
    }


    fun logOut() {
        _stateLiveData.value = State.LOADING
        Log.e("createUser", "name thread = " + Thread.currentThread().name)
        viewModelScope.launch {
            when (val result = useCaseLogOut()) {
                is Result.Value -> {
                    _stateLiveData.value = State.SUCCESS
                    // _liveDataResult.value = result.value
                }
                is Result.Error -> {
                    _stateLiveData.value = State.ERROR
                    _liveDataResult.value = result.error.message
                }
            }
        }
    }

    fun addFriend(name:String) {
        TODO("Not yet implemented")
    }
}