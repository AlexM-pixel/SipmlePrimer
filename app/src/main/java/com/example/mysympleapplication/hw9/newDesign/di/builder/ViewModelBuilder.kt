package com.example.mysympleapplication.hw9.newDesign.di.builder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mysympleapplication.hw9.newDesign.di.qualifaer.ViewModelKey
import com.example.mysympleapplication.hw9.newDesign.viewmodels.CreateUserByEmailViewModel
import com.example.mysympleapplication.hw9.newDesign.viewmodels.HomeFragmentViewModel
import com.example.mysympleapplication.hw9.newDesign.viewmodels.LoginViewModel
import com.example.mysympleapplication.hw9.newDesign.viewmodels.SettingsFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelBuilder {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(myViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateUserByEmailViewModel::class)
    abstract fun bindRegistrationViewModel(viewModel: CreateUserByEmailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeFragmentViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsFragmentViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsFragmentViewModel):ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}