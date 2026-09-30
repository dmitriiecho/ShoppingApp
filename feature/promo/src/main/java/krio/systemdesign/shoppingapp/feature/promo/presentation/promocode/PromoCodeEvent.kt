package krio.systemdesign.shoppingapp.feature.promo.presentation.promocode

sealed interface PromoCodeEvent {

    data class OnPromoCodeChange(val value: String) : PromoCodeEvent

    data object OnApplyClick : PromoCodeEvent
}
