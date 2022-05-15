package com.example.mysympleapplication.hw9.newDesign.di.module

import com.example.mysympleapplication.hw9.newDesign.data.mapper.SumSpendsOfMonthMapper
import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.repositories.login_repository.UserDataRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.login_repository.UserDataRepositoryImpl
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.AuthRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.AuthRepositoryImpl
import com.example.mysympleapplication.hw9.newDesign.data.db.db_sours.UsersDbSours
import com.example.mysympleapplication.hw9.newDesign.data.net.FirebaseAuthUserSource
import com.example.mysympleapplication.hw9.newDesign.data.net.FirestoreSource
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SpendsDataBaseRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SpendsDataBaseRepositoryImpl
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {
    @Provides
    fun provideAuthRepository(authSource: FirebaseAuthUserSource): AuthRepository {
        return AuthRepositoryImpl(authSource)
    }

    @Provides
    fun provideUsersDataRepository(usersDbSours: UsersDbSours,frSource: FirestoreSource): UserDataRepository {
        return UserDataRepositoryImpl(usersDbSours,frSource)
    }
    @Provides
    fun provideDataBaseRepository(db:AppDataBase, ofMonthMapper: SumSpendsOfMonthMapper):SpendsDataBaseRepository {
        return SpendsDataBaseRepositoryImpl(db,ofMonthMapper)
    }
}