package krio.systemdesign.shoppingapp.feature.cart.impl.analytics

import krio.systemdesign.shoppingapp.crossfeature.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.crossfeature.analytics.AnalyticsValue
import krio.systemdesign.shoppingapp.crossfeature.analytics.system.AnalyticsSystem

internal class CartClearedAnalyticsEvent : AnalyticsEvent("cart_cleared") {
    override val systems = setOf(AnalyticsSystem.Insights)

    override val params = emptyMap<String, AnalyticsValue>()
}
