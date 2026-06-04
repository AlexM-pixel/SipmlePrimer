package com.example.mysympleapplication.hw9.newDesign.domain.model

sealed class PartnerState {
    object None : PartnerState() // Нет партнера
    data class Sent(val email: String) : PartnerState() // Мы отправили приглашение
    data class Received(val email: String) : PartnerState() // Нам прислали приглашение
    data class Accepted(val name: String, val email: String, val avatarUrl: String) : PartnerState() // Подключены
}