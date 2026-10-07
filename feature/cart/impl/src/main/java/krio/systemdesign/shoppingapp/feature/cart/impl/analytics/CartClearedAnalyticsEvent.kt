package krio.systemdesign.shoppingapp.feature.cart.impl.analytics

import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsValue
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem

internal class CartClearedAnalyticsEvent : AnalyticsEvent("cart_cleared") {
    override val systems = setOf(AnalyticsSystem.Insights)

    override val params = emptyMap<String, AnalyticsValue>()
}
