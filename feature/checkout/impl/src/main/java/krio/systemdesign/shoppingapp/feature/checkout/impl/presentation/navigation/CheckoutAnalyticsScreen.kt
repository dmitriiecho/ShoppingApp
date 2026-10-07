package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen

fun NavDestination.checkoutAnalyticsScreen(): AnalyticsScreen? =
    if (hasRoute<CheckoutRoutes.Checkout>()) AnalyticsScreen.Checkout else null
