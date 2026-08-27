package krio.systemdesign.shoppingapp.feature.catalog.presentation.productdetails

sealed interface ProductDetailsEvent {

    data class OnAddToCart(
        val quantity: Int = 1,
    ) : ProductDetailsEvent

    data class OnUpdateCartQuantity(
        val quantity: Int,
    ) : ProductDetailsEvent

    data object OnRemoveFromCart : ProductDetailsEvent

    data object OnRetry : ProductDetailsEvent

    data object OnBackClick : ProductDetailsEvent
}
