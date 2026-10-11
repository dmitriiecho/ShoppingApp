package krio.systemdesign.shoppingapp.feature.settings.impl.presentation.navigation

import androidx.navigation3.runtime.NavKey
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen

fun NavKey.settingsAnalyticsScreen(): AnalyticsScreen? = if (this ==
    SettingsRoutes.Settings
) {
    AnalyticsScreen.Settings
} else {
    null
}
