package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object CheckoutRoutes {
    // The feature's only screen: the app opens the feature with it.
    @Serializable
    data object Checkout : NavKey
}

// The checkout feature's keys, for saving the back stack: the app adds them to its SavedStateConfiguration.
val checkoutNavKeys = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(CheckoutRoutes.Checkout::class)
    }
}
