package com.example.mysympleapplication.hw9.newDesign.di.builder

import com.example.mysympleapplication.hw9.newDesign.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [ViewModelBuilder::class])
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [FragmentsBuilder::class])
    abstract fun bindMapsActivity(): MainActivity
}