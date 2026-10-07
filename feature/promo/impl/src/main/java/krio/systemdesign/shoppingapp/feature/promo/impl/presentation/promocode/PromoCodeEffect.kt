package krio.systemdesign.shoppingapp.feature.promo.impl.presentation.promocode

import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

sealed interface PromoCodeEffect {

    data class CloseWithResult(val promoCode: PromoCode) : PromoCodeEffect

    data object NavigateBack : PromoCodeEffect
}
