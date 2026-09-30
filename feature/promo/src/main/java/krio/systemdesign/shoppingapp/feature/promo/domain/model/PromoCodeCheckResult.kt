package krio.systemdesign.shoppingapp.feature.promo.domain.model

import krio.systemdesign.shoppingapp.domain.model.PromoCode

sealed interface PromoCodeCheckResult {
    data class Valid(val promoCode: PromoCode) : PromoCodeCheckResult
    data object NotFound : PromoCodeCheckResult
    data class Error(val error: Throwable) : PromoCodeCheckResult
}
