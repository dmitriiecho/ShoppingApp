package krio.systemdesign.shoppingapp.feature.promo.impl.domain.repository

import krio.systemdesign.shoppingapp.feature.promo.impl.domain.model.PromoCodeCheckResult
import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

internal interface PromoCodeRepository {
    suspend fun checkPromoCode(code: String): PromoCodeCheckResult

    suspend fun getPromoCodes(): Result<List<PromoCode>>
}
