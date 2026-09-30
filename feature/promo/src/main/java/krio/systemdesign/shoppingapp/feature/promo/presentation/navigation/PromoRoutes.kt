package krio.systemdesign.shoppingapp.feature.promo.presentation.navigation

import kotlinx.serialization.Serializable

object PromoRoutes {
    @Serializable
    data class Graph(
        val resultKey: String,
    )

    @Serializable
    internal data object PromoCode
}
