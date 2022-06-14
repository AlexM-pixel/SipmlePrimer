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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.coroutines.coroutineContext


class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Handler(Looper.getMainLooper()).postDelayed({
            if (getUser()) {
                findNavController().navigate(R.id.action_splashFragment_to_bottomNavFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }, 2600)
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    fun getUser(): Boolean {
        val mAuth: FirebaseAuth =
            FirebaseAuth.getInstance()                  // dagger должен наверное предоставлять
        val user: FirebaseUser? = mAuth.currentUser
        return user != null
    }
}