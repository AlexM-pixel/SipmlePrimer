package com.example.mysympleapplication.hw9.newDesign.di.builder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.RuntimeException
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
 class ViewModelFactory @Inject constructor(
    private val viewModels: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var currentViewModel: Provider<out ViewModel>? = viewModels[modelClass]
        if (currentViewModel == null) {
            for ((key, value) in viewModels) {
                if (modelClass.isAssignableFrom(key)) {
                    currentViewModel = value
                    break
                }
            }
        }
        requireNotNull(currentViewModel){"Unknown model class $modelClass" }
        try {
            @Suppress("UNCHECKED_CAST")
            return currentViewModel.get() as T
        }catch (e:Exception){
            throw RuntimeException(e)
        }
    }
}