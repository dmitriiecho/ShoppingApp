package krio.systemdesign.shoppingapp.feature.promo.presentation.navigation

import kotlinx.serialization.Serializable

data class PromoCodeResult(
    val promoCode: String,
)

object PromoRoutes {
    @Serializable
    data class Graph(
        val resultKey: String,
    )

    @Serializable
    internal data object PromoCode
}
