package com.example.mysympleapplication.hw9.newDesign.ui

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mysympleapplication.R
import com.example.mysympleapplication.hw9.newDesign.base.BaseFragment
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FirestorageRepositoryImpl
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.DownloadImageUrlUseCase
import com.example.mysympleapplication.hw9.newDesign.utils.Config
import com.example.mysympleapplication.hw9.newDesign.utils.MainPrefs
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext


class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //  корутины для безопасной задержки
        MainPrefs.firstStart = true // Отмечаем, что онбординг пройден

        viewLifecycleOwner.lifecycleScope.launch {
            delay(2600) // Ждем 2.6 секунды
            when {
                // 1. ПЕРВЫЙ ЗАПУСК -> Идем знакомиться с котом
                MainPrefs.firstStart -> {
                    findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
                }
                // 2. ПОЛЬЗОВАТЕЛЬ АВТОРИЗОВАН -> Идем на главный экран
                getUser() -> {
                    findNavController().navigate(R.id.action_splashFragment_to_bottomNavFragment)
                }
                // 3. ПОЛЬЗОВАТЕЛЬ НЕ АВТОРИЗОВАН -> Идем на экран входа (логин/регистрация)
                else -> {
                    findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                }
            }
        }
    }

    private fun getUser(): Boolean {
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = mAuth.currentUser
        return user != null
    }
}
