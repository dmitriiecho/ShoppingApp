package krio.systemdesign.shoppingapp.feature.checkout.impl.presentation.navigation

import androidx.navigation3.runtime.NavKey
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen

fun NavKey.checkoutAnalyticsScreen(): AnalyticsScreen? = if (this ==
    CheckoutRoutes.Checkout
) {
    AnalyticsScreen.Checkout
} else {
    null
}
