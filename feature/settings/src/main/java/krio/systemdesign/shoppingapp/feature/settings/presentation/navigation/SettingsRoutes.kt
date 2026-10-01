package krio.systemdesign.shoppingapp.feature.settings.presentation.navigation

import kotlinx.serialization.Serializable

object SettingsRoutes {
    @Serializable
    data object Graph

    @Serializable
    internal data object Settings
}
