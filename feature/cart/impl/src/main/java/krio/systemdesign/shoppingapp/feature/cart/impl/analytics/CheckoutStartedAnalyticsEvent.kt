package krio.systemdesign.shoppingapp.feature.cart.impl.analytics

import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.analyticsParams
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem

internal class CheckoutStartedAnalyticsEvent(
    itemCount: Int,
    totalCents: Long,
) : AnalyticsEvent("checkout_started") {
    override val systems = setOf(AnalyticsSystem.Insights)

    override val params = analyticsParams {
        param("item_count", itemCount)
        param("total_cents", totalCents)
    }
}
