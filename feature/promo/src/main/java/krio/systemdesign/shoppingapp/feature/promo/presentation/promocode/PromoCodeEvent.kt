package krio.systemdesign.shoppingapp.feature.promo.presentation.promocode

sealed interface PromoCodeEvent {

    data class OnPromoCodeChange(val value: String) : PromoCodeEvent

    data object OnApplyClick : PromoCodeEvent

    // Нажатие на код в подсказке: он подставляется в поле, применяет его пользователь сам.
    data class OnAvailablePromoCodeClick(val code: String) : PromoCodeEvent

    data object OnRetryAvailablePromoCodes : PromoCodeEvent
}
