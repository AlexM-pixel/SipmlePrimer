package com.example.mysympleapplication.hw9.newDesign.domain.model

enum class State(var stateDescription:String) {
    SUCCESS("success"),
    LOADING("files download"),
    ERROR("error")
}