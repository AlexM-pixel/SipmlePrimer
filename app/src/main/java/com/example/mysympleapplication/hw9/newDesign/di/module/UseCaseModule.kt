package com.example.mysympleapplication.hw9.newDesign.di.module

import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SpendsDataBaseRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.login_repository.UserDataRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.AuthRepository
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.*
import dagger.Module
import dagger.Provides

@Module(includes = [RepositoryModule::class])
class UseCaseModule {

    @Provides
    fun provideLoginUseCase(repo: AuthRepository) = LoginByEmailAndPasswordUseCase(repo)

    @Provides
    fun provideLogOutUseCase(repo: AuthRepository) = LogOutUseCase(repo)

    @Provides
    fun provideCreateUserByEMailUseCase(repo: AuthRepository) =
        CreateUserByEmailAndPasswordUseCase(repo)

    @Provides
    fun provideCreateNewUserFirestore(repo: AuthRepository) = CreateNewUserFirestoreUseCase(repo)

    @Provides
    fun provideCheckDataUseCase(repo: UserDataRepository) = CheckDataFireStoreAndDbUseCase(repo)

    @Provides
    fun provideGetMonthExpensesUseCase(repo: SpendsDataBaseRepository) = GetMonthlyExpensesUseCase(repo)

}