package com.example.mysympleapplication.hw9.newDesign.di.builder


import com.example.mysympleapplication.hw9.newDesign.ui.*
import com.example.mysympleapplication.hw9.newDesign.ui.dialogues.DeleteDetailsDialog
import com.example.mysympleapplication.hw9.newDesign.ui.dialogues.DeleteSpendDialog
import com.example.mysympleapplication.hw9.newDesign.ui.dialogues.ResetPassDialogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class FragmentsBuilder {
    @ContributesAndroidInjector
    abstract fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeRegistrationFragment(): RegistrationFragment

    @ContributesAndroidInjector
    abstract fun contributeBottomNavFragment(): BottomNavFragment

    @ContributesAndroidInjector
    abstract fun contributeResetPassDialogFragment(): ResetPassDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeDeleteSpendDialogFragment(): DeleteSpendDialog

    @ContributesAndroidInjector
    abstract fun contributeDeleteDetailsDialogFragment(): DeleteDetailsDialog

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeStatisticsFragment(): StatisticsFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeSpendsMonthFragment(): MonthlySpendsFragment

    @ContributesAndroidInjector
    abstract fun contributeDetailMonthlyFragment(): DetailMonthlyFragment

    @ContributesAndroidInjector
    abstract fun contributeEditSpendFragment(): EditSpendFragment

    @ContributesAndroidInjector
    abstract fun contributeImagesFragment(): ImagesFragment

}