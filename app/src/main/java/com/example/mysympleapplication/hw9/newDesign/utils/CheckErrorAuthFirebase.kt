package com.example.mysympleapplication.hw9.newDesign.utils

import android.text.TextUtils

class CheckErrorAuthFirebase {
    fun checkLogInError(e: Exception): String {
        if (e.message!!.contains("There is no user record corresponding to this identifier")) {
            return "не зарегистрированный email"
        }
        if (e.message!!.contains("The password is invalid or the user does not have a password")) {
            return "неверный пароль !"
        }
        if (e.message!!.contains("You can immediately restore it by resetting your password or you can try again later.")) {
            return "Попробуйте позже или зайдите на почту для сброса пароля !"
        }
        return e.message.toString()
    }

    fun checkCreateUserByEmailError(e: Exception): String {
        if (e.message!!.contains("Password should be at least 6 characters")) {
            return "Пароль должен содержать минимум 6 символов"
        }
        if (e.message!!.contains("The email address is already in use by another account")) {
            return "Данный email уже зарегистрирован"
        }
        return e.message.toString()
    }

    fun checkResetPassByMailError(e: Exception): String {
        if (e.message!!.contains("The email address is badly formatted")) {
            return "Некорректный email"
        }
        return e.message.toString()
    }
}