package krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen

fun NavDestination.promoAnalyticsScreen(): AnalyticsScreen? =
    if (hasRoute<PromoRoutes.PromoCode>()) AnalyticsScreen.PromoCode else null
