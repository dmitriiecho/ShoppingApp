package krio.systemdesign.shoppingapp.feature.catalog.presentation.productlist

import krio.systemdesign.shoppingapp.domain.model.Product

sealed interface ProductListEvent {

    data class OnItemClick(val product: Product) : ProductListEvent

    data class OnAddToCart(
        val product: Product,
        val quantity: Int = 1,
    ) : ProductListEvent

    data class OnUpdateCartQuantity(
        val productId: String,
        val quantity: Int,
    ) : ProductListEvent

    data class OnRemoveFromCart(
        val productId: String,
    ) : ProductListEvent

    data class OnSearchQueryChanged(val query: String) : ProductListEvent

    data object OnClearSearch : ProductListEvent

    data object OnBackClick : ProductListEvent
}
