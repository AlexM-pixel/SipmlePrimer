package com.example.mysympleapplication.hw9.newDesign.di.component

import android.app.Application
import com.example.mysympleapplication.hw9.App
import com.example.mysympleapplication.hw9.newDesign.MyApp
import com.example.mysympleapplication.hw9.newDesign.di.builder.ActivityBuilder
import com.example.mysympleapplication.hw9.newDesign.di.module.ContextModule
import com.example.mysympleapplication.hw9.newDesign.di.module.FireBaseModule
import com.example.mysympleapplication.hw9.newDesign.di.module.StorageModule
import com.example.mysympleapplication.hw9.newDesign.di.module.UseCaseModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        FireBaseModule::class,
        ActivityBuilder::class,
        AndroidSupportInjectionModule::class,
        ContextModule::class,
        StorageModule::class,
        UseCaseModule::class
    ]
)
interface AppComponent : AndroidInjector<MyApp> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
}