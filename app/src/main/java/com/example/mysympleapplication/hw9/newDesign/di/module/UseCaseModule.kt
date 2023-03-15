package com.example.mysympleapplication.hw9.newDesign.di.module

import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.*
import com.example.mysympleapplication.hw9.newDesign.data.repositories.login_repository.UserDataRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.*
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
    fun provideGetNameSpendsListUseCase(repo: NameSpendsDbRepository) =
        InsertModelNameBySpendUseCase(repo)

    @Provides
    fun provideGetModelsNamesUseCase(repo: ModelsSpendsDbRepository) = GetCategoryPayUseCase(repo)

    @Provides
    fun provideSaveBalanceUseCase(repoDb: BalanceDbRepository, repoFr: BalanceFrRepository) =
        SaveBalanceUseCase(repoDb = repoDb, repoFr = repoFr)

    @Provides
    fun provideSavePostuplenieUseCase(
        repoDb: PostuplenieDbRepository,
        repoFr: PostuplenieFrRepository
    ) = SavePostuplenieUseCase(repoDb, repoFr)

    @Provides
    fun provideGetMonthlySpendsUseCase(repo: SpendsDbRepository) = GetMonthlySpendsUseCase(repo)

    @Provides
    fun provideGetDetailsSpendUseCase(repo: DetailsSpendDbRepository) =
        GetDetailsFlowUseCase(repo)
}