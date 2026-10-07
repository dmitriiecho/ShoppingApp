package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.cart

import krio.systemdesign.shoppingapp.shared.domain.model.PromoCode

sealed interface CartEvent {

    data class OnItemClick(
        val productId: String,
        val productName: String,
        val imageUrl: String,
    ) : CartEvent

    data class OnQuantityChange(
        val productId: String,
        val quantity: Int,
    ) : CartEvent

    data class OnRemoveFromCartClick(val productId: String) : CartEvent

    data object OnCheckoutClick : CartEvent

    data object OnPromoCodeClick : CartEvent

    data object OnRemovePromoCodeClick : CartEvent

    // The promo code screen returned the code it checked; the navigation passes it on.
    data class OnPromoCodeApplied(val promoCode: PromoCode) : CartEvent

    data object OnClearCartClick : CartEvent

    data object OnClearCartConfirmClick : CartEvent

    data object OnClearCartDialogDismiss : CartEvent

    // The cart screen became visible: its tab was opened or the user came back from another screen.
    data object OnScreenShown : CartEvent

    data object OnAcceptNewPricesClick : CartEvent

    data object OnRemoveUnavailableClick : CartEvent

    // There's no Back button in the tab; it's for when the screen is embedded in a flow the user can leave.
    data object OnBackClick : CartEvent
}
