package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen

fun NavDestination.cartAnalyticsScreen(): AnalyticsScreen? =
    if (hasRoute<CartRoutes.Cart>()) AnalyticsScreen.Cart else null
