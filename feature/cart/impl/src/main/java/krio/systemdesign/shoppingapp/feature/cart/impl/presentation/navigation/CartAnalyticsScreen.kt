package krio.systemdesign.shoppingapp.feature.cart.impl.presentation.navigation

import androidx.navigation3.runtime.NavKey
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen

fun NavKey.cartAnalyticsScreen(): AnalyticsScreen? = if (this == CartRoutes.Cart) AnalyticsScreen.Cart else null
