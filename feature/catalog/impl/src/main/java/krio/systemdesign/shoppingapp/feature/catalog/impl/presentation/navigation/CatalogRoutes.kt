package krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

// The arguments of the product details screen. The catalog and the cart open it with their own keys,
// so the screen opens in the tab it was opened from.
interface ProductDetailsRoute {
    val productId: String

    // Known before the product loads, so the screen shows them at once. Null when opened by a deep link.
    val productName: String?
    val imageUrl: String?
}

object CatalogRoutes {
    // The catalog's first screen: the app opens the feature with it.
    @Serializable
    data object ProductList : NavKey

    @Serializable
    internal data class ProductDetails(
        override val productId: String,
        override val productName: String? = null,
        override val imageUrl: String? = null,
    ) : ProductDetailsRoute,
        NavKey
}

// The catalog's keys, for saving the back stack: the app adds them to its SavedStateConfiguration.
val catalogNavKeys = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(CatalogRoutes.ProductList::class)
        subclass(CatalogRoutes.ProductDetails::class)
    }
}
