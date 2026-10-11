package krio.systemdesign.shoppingapp.feature.settings.impl.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object SettingsRoutes {
    // The feature's only screen: the app opens the feature with it.
    @Serializable
    data object Settings : NavKey
}

// The settings feature's keys, for saving the back stack: the app adds them to its SavedStateConfiguration.
val settingsNavKeys = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(SettingsRoutes.Settings::class)
    }
}
