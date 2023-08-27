package com.example.mysympleapplication.hw9.newDesign.di.module

import com.example.mysympleapplication.hw9.newDesign.data.db.AppDataBase
import com.example.mysympleapplication.hw9.newDesign.data.repositories.login_repository.UserDataRepository
import com.example.mysympleapplication.hw9.newDesign.data.repositories.login_repository.UserDataRepositoryImpl
import com.example.mysympleapplication.hw9.newDesign.data.db.db_sours.UsersDbSours
import com.example.mysympleapplication.hw9.newDesign.data.mapper.*
import com.example.mysympleapplication.hw9.newDesign.data.net.FirebaseAuthUserSource
import com.example.mysympleapplication.hw9.newDesign.data.net.FirestoreSource
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.*
import com.example.mysympleapplication.hw9.newDesign.data.repositories.net_repository.*
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideFriendDataRepository(
        fr: FirebaseFirestore,
        mapper: BalanceMapper,
        spendsMapper: SpendsMapper
    ): FriendsDataRepository {
        return FriendsDataRepositoryImpl(fr, mapper,spendsMapper)
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
    ): SumSpendsRepository {
        return SumSpendsRepositoryImpl(db, ofMonthMapper)
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

    @Provides
    fun provideBalanceDbRepository(
        db: AppDataBase,
        mapper: BalanceMapper
    ): BalanceDbRepository {
        return BalanceDbRepositoryImpl(db = db, mapper = mapper)
    }

    @Provides
    fun provideBalanceFrRepository(
        fr: FirebaseFirestore,
        mapper: BalanceMapper
    ): BalanceFrRepository {
        return BalanceFrRepositoryImpl(fr = fr, mapper)
    }

    @Provides
    fun providePostuplenieDbRepository(
        db: AppDataBase,
        mapper: PostuplenieMapper
    ): PostuplenieDbRepository {
        return PostuplenieDbRepositoryImpl(db = db, mapper = mapper)
    }

    @Provides
    fun providePostuplenieFrRepository(
        fr: FirebaseFirestore,
        mapper: PostuplenieMapper
    ): PostuplenieFrRepository {
        return PostuplenieFrRepoImpl(fr = fr, mapper)
    }

    @Provides
    fun provideDetailsSpendDbRepository(
        db: AppDataBase,
        mapper: DetailsSpendMapper
    ): DetailsSpendDbRepository {
        return DetailsSpendDbRepositoryImpl(db = db, mapper = mapper)
    }

    @Provides
    fun provideDetailsSpendFrRepository(
        fr: FirebaseFirestore,
        mapper: DetailsSpendMapper
    ): DetailsSpendFrRepository {
        return DetailsSpendFrRepositoryImpl(fr = fr, mapper)
    }
}