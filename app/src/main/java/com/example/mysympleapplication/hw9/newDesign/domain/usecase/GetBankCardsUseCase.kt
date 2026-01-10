package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.BankCardDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.BankCard
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetBankCardsUseCase @Inject constructor(
    private val repository: BankCardDbRepository
) {

    operator fun invoke(): Flow<Resource<List<BankCard>>> {
        return repository.getCards() // 1. Берем существующий Flow<List<BankCard>>
            .map { list ->
                // 2. Превращаем пришедший List в Resource.Success
                Resource.Success(list) as Resource<List<BankCard>>
            }
            .onStart {
                // 3. Перед тем как пойдут данные, отправляем Loading
                emit(Resource.Loading())
            }
            .catch { e ->
                // 4. Если в потоке случилась ошибка, отправляем Error
                emit(Resource.Error(message = e.message.toString()))
            }
    }
}