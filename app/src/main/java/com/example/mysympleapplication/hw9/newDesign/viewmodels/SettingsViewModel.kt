package com.example.mysympleapplication.hw9.newDesign.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysympleapplication.hw9.newDesign.domain.model.PartnerState
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.*
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    observePartnerStateUseCase: ObservePartnerStateUseCase,
    private val sendPartnerInviteUseCase: SendPartnerInviteUseCase,
    private val managePartnerInviteUseCase: ManagePartnerInviteUseCase,
    private val getFriendProfileUseCase: GetFriendProfileUseCase
) : ViewModel() {

    private val _partnerState = MutableLiveData<PartnerState>()
    val partnerState: LiveData<PartnerState> get() = _partnerState

    // LiveData для сообщений пользователю (Toast)
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    init {
        // Подписываемся на состояние партнера при запуске
        observePartnerStateUseCase(MainPrefs.mailUser).onEach { state ->
            if (state is PartnerState.Accepted) {
                // Связь установлена! Сохраняем почту для других экранов
                MainPrefs.mailFriend = state.email
                loadPartnerProfile(state.email) // Подгружаем аватарку и имя
            } else {
                // Связи нет (или ожидание) -> сбрасываем чужую почту из настроек
                MainPrefs.mailFriend = ""
                _partnerState.value = state
            }
        }.launchIn(viewModelScope)
    }

    private fun loadPartnerProfile(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val res = getFriendProfileUseCase(email)) {
                is Result.Value -> {
                    _partnerState.postValue(PartnerState.Accepted(res.value.first, email, res.value.second))
                }
                is Result.Error -> {
                    _partnerState.postValue(PartnerState.Accepted(email.substringBefore("@"), email, "ic_baseline_person_24"))
                }
            }
        }
    }

    // ==========================================
    // ЛОГИКА ПАРТНЕРА (ИНВАЙТЫ)
    // ==========================================

    fun sendInvite(friendEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sendPartnerInviteUseCase(MainPrefs.mailUser, friendEmail)
        }
    }

    fun respondToInvite(friendEmail: String, accept: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            managePartnerInviteUseCase(MainPrefs.mailUser, friendEmail, accept)
        }
    }

    // ==========================================
    // ЛОГИКА ПРОФИЛЯ (ОБНОВЛЕНИЕ СЕБЯ)
    // ==========================================

    fun updateMyProfile(newName: String? = null, newAvatarName: String? = null) {
        val email = MainPrefs.mailUser
        if (email.isEmpty()) return

        val updates = hashMapOf<String, Any>()
        if (newName != null) updates["name"] = newName
        if (newAvatarName != null) updates["avatarName"] = newAvatarName

        if (updates.isEmpty()) return

        // По-хорошему это тоже нужно вынести в UseCase (UpdateUserProfileUseCase),
        // но для простоты пока оставим тут
        FirebaseFirestore.getInstance()
            .collection(email)
            .document("USERNAME") // или UserDocuments.USERNAME.name
            .set(updates, SetOptions.merge())
            .addOnSuccessListener {
                _toastMessage.postValue("Профиль обновлен ☁️")
            }
            .addOnFailureListener {
                _toastMessage.postValue("Ошибка синхронизации")
            }
    }
}