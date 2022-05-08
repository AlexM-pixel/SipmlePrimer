package com.example.mysympleapplication.hw9.newDesign.viewmodels

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.newDesign.domain.model.State
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.CreateNewUserFirestoreUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.CreateUserByEmailAndPasswordUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.CheckErrorAuthFirebase
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateUserByEmailViewModel @Inject constructor(
    private val useCaseCreateByEmail: CreateUserByEmailAndPasswordUseCase,
    private val useCaseCreateUserFirestore: CreateNewUserFirestoreUseCase
) : ViewModel() {

    private val _stateLiveData = MutableLiveData<State>()
    val stateLiveData: LiveData<State> get() = _stateLiveData
    private val _liveDataResult = MutableLiveData<String>()
    val liveDataResult: LiveData<String> get() = _liveDataResult

    fun createUserByEmail(userName: String, mail: String, pass: String) {
        if (!validateForm(userName, mail, pass)) {
            return
        }
        viewModelScope.launch {
            _stateLiveData.value = State.LOADING
            when (val result = useCaseCreateByEmail(email = mail, password = pass)) {
                is Result.Value -> {
                    createNewUserInFireStore(userName = userName, mail = mail)
                }
                is Result.Error -> {
                    _stateLiveData.value = State.ERROR
                    _liveDataResult.value =
                        CheckErrorAuthFirebase().checkCreateUserByEmailError(result.error)
                }
            }
        }
    }

    private fun createNewUserInFireStore(userName: String, mail: String) {
        viewModelScope.launch {
            when (val result = useCaseCreateUserFirestore(userName, mail)) {
                is Result.Value -> {
                    _stateLiveData.value = State.SUCCESS
                }
                is Result.Error -> {
                      //если ошибка по созданю юзера в базе, то надо удалить и  почту так как она уже создалась и заново пользователь не сможет зарегиться с этим емэйлом
                    _stateLiveData.value = State.ERROR
                    _liveDataResult.value = result.error.message
                }
            }
        }
    }

    private fun validateForm(
        userName: String,
        userMail: String,
        userPass: String
    ): Boolean {
        val validEmail = "^[a-zA-Z0-9.]{1,}[@]{1}[a-z]{2,7}[.]{1}+[a-z]{2,6}".toRegex()
        if (TextUtils.isEmpty(userName)) {
            _stateLiveData.value = State.ERROR
            _liveDataResult.value = "Напишите имя пользователя"
            return false
        }
        if (TextUtils.isEmpty(userMail) || TextUtils.isEmpty(userPass)) {
            Log.e("createUser", "TextUtils: ")
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