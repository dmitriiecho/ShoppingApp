package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation

import kotlinx.serialization.Serializable

// The arguments of the product details screen. The catalog and the cart open it with their own routes,
// so the screen opens in the tab it was opened from.
interface ProductDetailsRoute {
    val productId: String

    // Known before the product loads, so the screen shows them at once. Null when opened by a deep link.
    val productName: String?
    val imageUrl: String?
}

object CatalogRoutes {
    @Serializable
    data object Graph

    @Serializable
    internal data object ProductList

    @Serializable
    internal data class ProductDetails(
        override val productId: String,
        override val productName: String? = null,
        override val imageUrl: String? = null,
    ) : ProductDetailsRoute
}
