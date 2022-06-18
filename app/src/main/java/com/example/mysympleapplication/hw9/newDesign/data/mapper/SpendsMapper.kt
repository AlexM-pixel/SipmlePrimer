package com.example.mysympleapplication.hw9.newDesign.data.mapper

import android.util.Log
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.NameSpendsEntity
import com.example.mysympleapplication.hw9.newDesign.domain.model.Spend
import com.example.mysympleapplication.hw9.newDesign.data.entity_model.SpendEntity
import com.example.mysympleapplication.hw9.newDesign.data.repositories.db_repository.NameSpendsDbRepository
import com.example.mysympleapplication.hw9.newDesign.domain.model.NameSpend
import com.example.mysympleapplication.hw9.newDesign.domain.usecase.GetCategoryPayUseCase
import javax.inject.Inject

class SpendsMapper @Inject constructor(private val getCategoryPay: GetCategoryPayUseCase) :
    EntityMapper<SpendEntity, Spend> {
    override fun mapFromEntity(entity: SpendEntity): Spend {
        return Spend(
            id = entity.id,
            spendName = entity.spendName,
            value = entity.value,
            date = entity.date,
            url = null
        )
    }


    override fun mapToEntity(domainModel: Spend): SpendEntity {
        return SpendEntity(
            id = domainModel.id,
            spendName = domainModel.spendName,
            value = domainModel.value,
            date = domainModel.date
        )
    }

    fun susMapFromEntity(entity: SpendEntity, allNamesSpends: List<NameSpend>): Spend {
        return Spend(
            id = entity.id,
            spendName = entity.spendName,
            value = entity.value,
            date = entity.date,
            url = getNameImage(entity.spendName, allNamesSpends) ?: "produkti"
        )
    }

    private fun getNameImage(spendName: String, listCategory: List<NameSpend>): String? {
        for (category in listCategory) {
            if (spendName.equals(category.ruName)) {
                //    val uri = repoFrStorage.downloadImageUrl(category.imageName)
                Log.e("getNameImageByMapper", "categoryPay=${category.ruName}, imageName=${category.imageName}")
                return category.imageName
            }
        }
        return null
    }

    suspend fun fromEntityList(entityList: List<SpendEntity>): List<Spend> {
        val listCategory = getCategoryPay.getModelsSpends()
        return entityList.map { susMapFromEntity(it, listCategory) }
    }
}