package com.example.mysympleapplication.hw9.newDesign.di.module

import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.ModelsSpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.NameSpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SumMonthlySpendsRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.login_repository.UserDataRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.AuthRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FireStoreRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.FirestorageRepository
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
    fun provideGetMonthExpensesUseCase(repo: SumMonthlySpendsRepository) =
        GetMonthlyExpensesUseCase(repo)

    @Provides
    fun provideSaveDBSpendUseCase(repo: SpendsDbRepository) = SaveSpendDbUseCase(repo)

    @Provides
    fun provideSaveFrStoreSpendUseCase(repo: FireStoreRepository) = SaveSpendFrStoreUseCase(repo)

    @Provides
    fun provideDownloadImageUriUseCase(repo: FirestorageRepository) = DownloadImageUrlUseCase(repo)

    @Provides
    fun provideGetNameSpendsListUseCase(repo: NameSpendsDbRepository) = GetNameSpendsListUseCase(repo)

    @Provides
    fun provideGetModelsNamesUseCase(repo: ModelsSpendsDbRepository) = GetModelsSpendsUseCase(repo)
}