package krio.systemdesign.shoppingapp.navigation.bottombar

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation.CartRoutes
import krio.systemdesign.shoppingapp.feature.catalog.impl.presentation.navigation.CatalogRoutes
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.navigation.SettingsRoutes

// The tabs of the bottom bar. Each one has its own back stack, starting at root.
@Serializable
enum class BottomTab(val root: NavKey) {
    Catalog(CatalogRoutes.ProductList),
    Cart(CartRoutes.Cart),
    Settings(SettingsRoutes.Settings),
}
