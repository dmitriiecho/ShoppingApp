package krio.systemdesign.shoppingapp.navigation.bottombar

import kotlinx.serialization.Serializable

object BottomNavRoutes {
    @Serializable
    data object CatalogTab

    @Serializable
    data object CartTab

    @Serializable
    data object SettingsTab
}
