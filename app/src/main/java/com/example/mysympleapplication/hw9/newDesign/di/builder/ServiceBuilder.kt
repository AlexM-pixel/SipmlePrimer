package com.example.mysympleapplication.hw9.newDesign.di.builder

import com.example.mysympleapplication.hw9.newDesign.domain.background.BankSmsService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBuilder {
    @ContributesAndroidInjector
    abstract fun contributeService():BankSmsService
}