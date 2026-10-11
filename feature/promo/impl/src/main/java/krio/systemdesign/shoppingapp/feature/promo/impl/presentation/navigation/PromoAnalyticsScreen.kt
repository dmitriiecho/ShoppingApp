package krio.systemdesign.shoppingapp.feature.promo.impl.presentation.navigation

import androidx.navigation3.runtime.NavKey
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen

fun NavKey.promoAnalyticsScreen(): AnalyticsScreen? =
    if (this is PromoRoutes.PromoCode) AnalyticsScreen.PromoCode else null
