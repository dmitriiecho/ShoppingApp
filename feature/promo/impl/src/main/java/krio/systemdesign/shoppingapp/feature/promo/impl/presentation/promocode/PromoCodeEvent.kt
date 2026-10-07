package krio.systemdesign.shoppingapp.feature.promo.impl.presentation.promocode

internal sealed interface PromoCodeEvent {

    data object OnApplyClick : PromoCodeEvent

    // Puts a code from the hint into the field; the user applies it themselves.
    data class OnAvailablePromoCodeClick(val code: String) : PromoCodeEvent

    data object OnRetryAvailableCodesClick : PromoCodeEvent

    data object OnBackClick : PromoCodeEvent
}
