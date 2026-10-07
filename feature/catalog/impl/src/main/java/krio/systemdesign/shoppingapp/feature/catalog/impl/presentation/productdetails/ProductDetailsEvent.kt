package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productdetails

internal sealed interface ProductDetailsEvent {

    data object OnAddToCartClick : ProductDetailsEvent

    data class OnQuantityChange(val quantity: Int) : ProductDetailsEvent

    data object OnRemoveFromCartClick : ProductDetailsEvent

    data object OnRetryClick : ProductDetailsEvent

    data object OnBackClick : ProductDetailsEvent
}
