package krio.systemdesign.shoppingapp.feature.catalog.presentation.navigation

import kotlinx.serialization.Serializable

interface ProductDetailsRoute {
    val productId: String
    val productName: String
}

object CatalogRoutes {
    @Serializable
    data object Graph

    @Serializable
    internal data object ProductList

    @Serializable
    internal data class ProductDetails(
        override val productId: String,
        override val productName: String = "",
    ) : ProductDetailsRoute
}
