package krio.systemdesign.shoppingapp.feature.settings.impl.presentation.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen

fun NavDestination.settingsAnalyticsScreen(): AnalyticsScreen? =
    if (hasRoute<SettingsRoutes.Settings>()) AnalyticsScreen.Settings else null
