package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation

import kotlinx.serialization.Serializable

object CheckoutRoutes {
    @Serializable
    data object Graph

    @Serializable
    internal data object Checkout
}
