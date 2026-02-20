package com.example.mysympleapplication.hw9.newDesign.domain.usecase

import com.example.mysympleapplication.hw9.SumSpendsOfMonth
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.SumSpendsRepository
import com.example.mysympleapplication.hw9.newDesign.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class GetMonthlyExpensesUseCase @Inject constructor(
    private val repo: SumSpendsRepository
) {
    operator fun invoke(): Flow<Resource<List<SumSpendsOfMonth>>> = flow {
        // 1. Отправляем загрузку
        emit(Resource.Loading())

        // 2. Делаем запрос. Если тут будет ошибка — она улетит в блок .catch ниже
        val response = repo.getSumSpendsOfMonth()

        // 3. Отправляем успех.
        // Если ошибка случится ЗДЕСЬ (в UI после получения данных),
        // она НЕ попадет в .catch, и приложение честно упадет с NPE,
        // показав реальную причину.
        emit(Resource.Success(response))

    }.catch { e ->
        // Этот блок ловит ошибки ТОЛЬКО из репозитория
        emit(Resource.Error(message = e.toString()))
    }
}

