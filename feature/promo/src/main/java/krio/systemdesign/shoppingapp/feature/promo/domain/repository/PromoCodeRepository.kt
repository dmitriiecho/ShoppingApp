package krio.systemdesign.shoppingapp.feature.promo.domain.repository

import krio.systemdesign.shoppingapp.domain.model.PromoCode

interface PromoCodeRepository {
    suspend fun getPromoCode(code: String): Result<PromoCode>
}
