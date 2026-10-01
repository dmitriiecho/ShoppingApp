package krio.systemdesign.shoppingapp.feature.cart.presentation.cart

sealed interface CartEvent {

    data class OnItemClick(
        val productId: String,
        val productName: String,
    ) : CartEvent

    data class OnUpdateQuantity(
        val productId: String,
        val quantity: Int,
    ) : CartEvent

    data class OnRemoveItem(
        val productId: String,
    ) : CartEvent

    data object OnCheckoutClick : CartEvent

    data object OnPromoClick : CartEvent

    data object OnRemovePromoClick : CartEvent

    data object OnClearCartClick : CartEvent

    data object OnClearCartConfirmed : CartEvent

    data object OnClearCartDismiss : CartEvent

    // Экран корзины стал видимым: открыли вкладку или вернулись с другого экрана.
    data object OnScreenShown : CartEvent

    data object OnAcceptNewPricesClick : CartEvent

    data object OnRemoveUnavailableClick : CartEvent
}
