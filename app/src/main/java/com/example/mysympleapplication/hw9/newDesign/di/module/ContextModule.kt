package com.example.mysympleapplication.hw9.newDesign.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContextModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application
    }
}