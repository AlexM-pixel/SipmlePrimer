package com.example.mysympleapplication.hw9.newDesign.di.module

import com.example.mysympleapplication.hw9.newDesign.data.mapper.SumSpendsOfMonthMapper
import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.repositories.login_repository.UserDataRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.login_repository.UserDataRepositoryImpl
import com.example.mysympleapplication.hw9.newDesign.data.db.db_sours.UsersDbSours
import com.example.mysympleapplication.hw9.newDesign.data.mapper.NameSpendsMapper
import com.example.mysympleapplication.hw9.newDesign.data.mapper.SpendsMapper
import com.example.mysympleapplication.hw9.newDesign.data.net.FirebaseAuthUserSource
import com.example.mysympleapplication.hw9.newDesign.data.net.FirestoreSource
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.*
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.*
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {
    @Provides
    fun provideAuthRepository(authSource: FirebaseAuthUserSource): AuthRepository {
        return AuthRepositoryImpl(authSource)
    }

    @Provides
    fun provideUsersDataRepository(
        usersDbSours: UsersDbSours,
        frSource: FirestoreSource
    ): UserDataRepository {
        return UserDataRepositoryImpl(usersDbSours, frSource)
    }

    @Provides
    fun provideMonthlySpendsRepository(
        db: AppDataBase,
        ofMonthMapper: SumSpendsOfMonthMapper
    ): SumMonthlySpendsRepository {
        return SumMonthlySpendsRepositoryImpl(db, ofMonthMapper)
    }

    @Provides
    fun provideDbSpendsRepository(
        db: AppDataBase,
        ofSpendsMapper: SpendsMapper
    ): SpendsDbRepository {
        return SpendsDbRepositoryImpl(db, ofSpendsMapper)
    }

    @Provides
    fun provideFireStoreRepository(
        frSource: FirestoreSource,
        mapper: SpendsMapper
    ): FireStoreRepository {
        return FireStoreRepositoryImpl(frSource, mapper)
    }

    @Provides
    fun provideFrStorageRepository(imageRef: StorageReference): FirestorageRepository {
        return FirestorageRepositoryImpl(refStorage = imageRef)
    }

    @Provides
    fun provideNameSpendsDbRepository(
        db: AppDataBase,
        mapper: NameSpendsMapper
    ): NameSpendsDbRepository {
        return NameSpendsDbRepositoryImpl(db = db, mapper = mapper)
    }
    @Provides
    fun provideModelsSpendsDbRepository(
        db: AppDataBase,
        mapper: NameSpendsMapper
    ): ModelsSpendsDbRepository {
        return ModelsSpendsDbRepositoryImpl(db = db, mapper = mapper)
    }
}