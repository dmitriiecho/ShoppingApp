package krio.systemdesign.shoppingapp.feature.promo.domain.repository

import krio.systemdesign.shoppingapp.feature.promo.domain.model.PromoCodeCheckResult

interface PromoCodeRepository {
    suspend fun checkPromoCode(code: String): PromoCodeCheckResult
}
