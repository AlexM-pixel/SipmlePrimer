package com.example.mysympleapplication.hw9.newDesign.di.module

import com.example.mysympleapplication.hw9.newDesign.utils.Config.STORAGE_IMAGE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides

@Module
class FireBaseModule {
    @Provides
    fun provideFireBaseAuth() = FirebaseAuth.getInstance()

    @Provides
    fun provideFireBaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    fun provideFireStorageRef() = FirebaseStorage.getInstance().getReference("image")

}