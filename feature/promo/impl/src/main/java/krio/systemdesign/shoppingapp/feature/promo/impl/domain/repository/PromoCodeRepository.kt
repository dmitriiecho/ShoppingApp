package krio.systemdesign.shoppingapp.feature.promo.impl.domain.repository

import krio.systemdesign.shoppingapp.crossfeature.domain.model.PromoCode
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.model.PromoCodeCheckResult

internal interface PromoCodeRepository {
    suspend fun checkPromoCode(code: String): PromoCodeCheckResult

    suspend fun getPromoCodes(): Result<List<PromoCode>>
}
