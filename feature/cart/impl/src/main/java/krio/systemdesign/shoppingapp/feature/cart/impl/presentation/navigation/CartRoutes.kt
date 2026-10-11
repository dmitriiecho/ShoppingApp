package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object CartRoutes {
    // The cart's first screen: the app opens the feature with it.
    @Serializable
    data object Cart : NavKey
}

internal object CartResults {
    const val PROMO_RESULT_KEY = "cart_promo_result"
}

// The cart's keys, for saving the back stack: the app adds them to its SavedStateConfiguration.
val cartNavKeys = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(CartRoutes.Cart::class)
    }
}
