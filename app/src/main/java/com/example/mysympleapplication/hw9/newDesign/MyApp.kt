package com.example.mysympleapplication.hw9.newDesign

import com.chibatching.kotpref.Kotpref
import com.example.mysympleapplication.hw9.newDesign.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class MyApp : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent
            .builder()
            .application(this)
            .build()
    }
}
