package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation

import kotlinx.serialization.Serializable

object CartRoutes {
    @Serializable
    data object Graph

    @Serializable
    internal data object Cart
}

internal object CartResults {
    const val PROMO_RESULT_KEY = "cart_promo_result"
}
