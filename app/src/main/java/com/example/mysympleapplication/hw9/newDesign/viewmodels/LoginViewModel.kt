package com.example.mysympleapplication.hw9.newDesign.viewmodels

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.*
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.CheckDataFireStoreAndDbUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.LogOutUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.LoginByEmailAndPasswordUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.ResetPasswUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.CheckErrorAuthFirebase
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import com.example.mysympleapplication.hw9.newDesign.utils.Config.COUNT_ERRORS_PASSW
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import kotlinx.coroutines.launch
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import kotlinx.coroutines.Dispatchers

import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val useCaseLogin: LoginByEmailAndPasswordUseCase,
    private val useCaseLogOut: LogOutUseCase,
    private val useCaseResetPass: ResetPasswUseCase,
    private val checkDataFireStoreAndDbUseCase: CheckDataFireStoreAndDbUseCase
) : ViewModel() {
    var countErrors = 0

    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData
    private val _liveDataResult = MutableLiveData<String>()
    val liveDataResult: LiveData<String> get() = _liveDataResult
    private val _liveDataCount = MutableLiveData<Int>()
    val liveDataCount: LiveData<Int> get() = _liveDataCount

    fun logInByEmail(mail: String, pass: String) {
        if (!validateForm(mail, pass)) {
            return
        }
        viewModelScope.launch {
            _stateLiveData.value = State.LOADING
            when (val result = useCaseLogin(email = mail, password = pass)) {
                is Result.Value -> {
                    checkDataFireStoreAndDbUseCase(email = mail)
                    MainPrefs.mailUser = mail
                    _stateLiveData.value = State.SUCCESS
                }
                is Result.Error -> {
                    if (CheckErrorAuthFirebase().checkLogInError(result.error)
                            .equals("неверный пароль !")
                    ) countErrors++
                    _liveDataCount.value = countErrors
                    _stateLiveData.value = State.ERROR
                    if (countErrors != COUNT_ERRORS_PASSW) {
                        _liveDataResult.value =
                            CheckErrorAuthFirebase().checkLogInError(result.error)
                    }
                }
            }
        }
    }



    fun resetPasswByEmail(email: String) {
        if (TextUtils.isEmpty(email)) {
            _liveDataResult.value = "напишите почту"
            return
        }
        _stateLiveData.value = State.LOADING
        viewModelScope.launch {
            when (val result = useCaseResetPass(email = email)) {
                is Result.Value -> {
                    _stateLiveData.value = State.SUCCESS
                    Log.e("createUser", "resetPassword Success: ${result.value}")
                }
                is Result.Error -> {
                    Log.e("createUser", "resetPassword: " + result.error.message)
                    _stateLiveData.value = State.ERROR
                    _liveDataResult.value =
                        CheckErrorAuthFirebase().checkResetPassByMailError(result.error)
                }
            }
        }
    }

    private fun validateForm(
        userMail: String,
        userPass: String
    ): Boolean {
        val validEmail = "^[a-zA-Z0-9.]{1,}[@]{1}[a-z]{2,7}[.]{1}+[a-z]{2,6}".toRegex()
        if (TextUtils.isEmpty(userMail) || TextUtils.isEmpty(userPass)) {
            _liveDataResult.value = "заполните все поля"
            return false
        }
        if (!userMail.matches(validEmail)) {
            _stateLiveData.value = State.ERROR
            Log.e("createUser", "matches: ")
            _liveDataResult.value = "не корректный email"
            return false
        }
        return true
    }


}