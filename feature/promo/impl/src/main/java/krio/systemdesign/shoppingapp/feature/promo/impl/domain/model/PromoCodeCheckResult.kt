package krio.systemdesign.shoppingapp.feature.promo.impl.domain.model

import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

internal sealed interface PromoCodeCheckResult {
    data class Valid(val promoCode: PromoCode) : PromoCodeCheckResult
    data object NotFound : PromoCodeCheckResult
    data class Error(val error: Throwable) : PromoCodeCheckResult
}
