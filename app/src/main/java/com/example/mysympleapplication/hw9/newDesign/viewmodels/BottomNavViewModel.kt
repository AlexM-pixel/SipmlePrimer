package com.example.mysympleapplication.hw9.newDesign.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.newDesign.domain.model.PartnerState
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.ManagePartnerInviteUseCase
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.ObservePartnerStateUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class BottomNavViewModel @Inject constructor(
    observePartnerStateUseCase: ObservePartnerStateUseCase,
    private val managePartnerInviteUseCase: ManagePartnerInviteUseCase
) : ViewModel() {

    // Подписываемся на базу данных. asLiveData() автоматически запустит корутину
    // и будет транслировать статус партнера всё время, пока жив BottomNavFragment
    val partnerState: LiveData<PartnerState> =
        observePartnerStateUseCase(MainPrefs.mailUser).asLiveData()

    // ---  МЕТОД ДЛЯ ОТВЕТА НА ЗАЯВКУ ---
    fun respondToInvite(friendEmail: String, accept: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            managePartnerInviteUseCase(MainPrefs.mailUser, friendEmail, accept)
        }
    }
}