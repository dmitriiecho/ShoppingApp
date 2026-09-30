package krio.systemdesign.shoppingapp.feature.promo.data.repository

import krio.systemdesign.shoppingapp.domain.model.PromoCode
import krio.systemdesign.shoppingapp.feature.promo.data.api.PromoApi
import krio.systemdesign.shoppingapp.feature.promo.data.dto.toDomain
import krio.systemdesign.shoppingapp.feature.promo.domain.repository.PromoCodeRepository
import javax.inject.Inject

class PromoCodeRepositoryImpl @Inject constructor(
    private val api: PromoApi,
) : PromoCodeRepository {
    override suspend fun getPromoCode(code: String): Result<PromoCode> {
        return try {
            Result.success(api.getPromoCode(code).toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
