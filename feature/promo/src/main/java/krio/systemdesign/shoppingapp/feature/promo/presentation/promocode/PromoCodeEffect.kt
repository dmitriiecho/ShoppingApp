package krio.systemdesign.shoppingapp.feature.promo.presentation.promocode

import krio.systemdesign.shoppingapp.domain.model.PromoCode

sealed interface PromoCodeEffect {

    data class CloseWithResult(val promoCode: PromoCode) : PromoCodeEffect
}
