package krio.systemdesign.shoppingapp.navigation

import krio.systemdesign.shoppingapp.feature.catalog.presentation.navigation.ProductDetailsRoute
import kotlinx.serialization.Serializable

@Serializable
data class CustomProductRoute(
    override val productId: String,
    override val productName: String = "",
    override val imageUrl: String = "",
) : ProductDetailsRoute
