package krio.systemdesign.shoppingapp.navigation

import krio.systemdesign.shoppingapp.feature.catalog.presentation.navigation.ProductDetailsRoute
import kotlinx.serialization.Serializable

// Product details opened from the cart: stays in the cart tab.
@Serializable
data class CartProductRoute(
    override val productId: String,
    override val productName: String = "",
    override val imageUrl: String = "",
) : ProductDetailsRoute
