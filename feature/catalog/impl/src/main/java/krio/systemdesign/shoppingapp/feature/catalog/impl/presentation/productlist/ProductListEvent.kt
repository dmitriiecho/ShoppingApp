package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.productlist

import krio.systemdesign.shoppingapp.shared.domain.model.Product

internal sealed interface ProductListEvent {

    data class OnProductClick(val product: Product) : ProductListEvent

    data class OnAddToCartClick(val product: Product) : ProductListEvent

    data class OnQuantityChange(
        val productId: String,
        val quantity: Int,
    ) : ProductListEvent

    data class OnRemoveFromCartClick(val productId: String) : ProductListEvent

    data class OnFirstVisibleItemChange(val index: Int) : ProductListEvent

    // There's no Back button in the tab; it's for when the screen is embedded in a flow the user can leave.
    data object OnBackClick : ProductListEvent
}
