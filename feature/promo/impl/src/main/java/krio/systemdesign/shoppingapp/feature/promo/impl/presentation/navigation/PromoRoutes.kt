package krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object PromoRoutes {
    // The promo code screen, the feature's first: the app opens the feature with it. resultKey works like
    // a request code: the caller passes it and gets the applied code back under it.
    @Serializable
    data class PromoCode(val resultKey: String) : NavKey
}

// The promo feature's keys, for saving the back stack: the app adds them to its SavedStateConfiguration.
val promoNavKeys = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(PromoRoutes.PromoCode::class)
    }
}
