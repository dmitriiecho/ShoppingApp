package krio.systemdesign.shoppingapp.navigation

import kotlinx.serialization.Serializable

object BottomNavRoutes {
    @Serializable
    data object CatalogTab

    @Serializable
    data object CartTab
}
