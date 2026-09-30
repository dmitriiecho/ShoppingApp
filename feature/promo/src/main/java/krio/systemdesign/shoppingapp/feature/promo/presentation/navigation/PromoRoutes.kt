package krio.systemdesign.shoppingapp.feature.promo.presentation.navigation

import krio.systemdesign.shoppingapp.domain.model.PromoCode
import kotlinx.serialization.Serializable

data class PromoCodeResult(
    val promoCode: PromoCode,
)

object PromoRoutes {
    @Serializable
    data class Graph(
        val resultKey: String,
    )

    @Serializable
    internal data object PromoCode
}
