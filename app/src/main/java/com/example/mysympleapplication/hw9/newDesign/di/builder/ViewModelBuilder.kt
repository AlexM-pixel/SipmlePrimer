package com.example.mysympleapplication.hw9.newDesign.di.builder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mysympleapplication.hw9.newDesign.di.qualifaer.ViewModelKey
import com.example.mysympleapplication.hw9.newDesign.ui.AddManualSpendFragment
import com.example.mysympleapplication.hw9.newDesign.viewmodels.*
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
    abstract fun bindSettingsViewModel(viewModel: SettingsFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MonthlySpendsViewModel::class)
    abstract fun bindMonthlySpendsViewModel(viewModel: MonthlySpendsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailMonthlyViewModel::class)
    abstract fun bindMDetailMonthlyViewModel(viewModel: DetailMonthlyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditSpendViewModel::class)
    abstract fun bindEditSpendViewModelViewModel(viewModel: EditSpendViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ImagesViewModel::class)
    abstract fun bindImagesViewModel(viewModel: ImagesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ManuallyAddSpendViewModel::class)
    abstract fun bindAddSpendMViewModel(viewModel: ManuallyAddSpendViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}