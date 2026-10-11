package krio.systemdesign.shoppingapp.feature.checkout.impl.analytics

import krio.systemdesign.shoppingapp.crossfeature.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.crossfeature.analytics.analyticsParams
import krio.systemdesign.shoppingapp.crossfeature.analytics.system.AnalyticsSystem

internal class OrderPlacedAnalyticsEvent(
    itemCount: Int,
    totalCents: Long,
    promoCode: String?,
) : AnalyticsEvent("order_placed") {
    override val systems = setOf(AnalyticsSystem.Insights, AnalyticsSystem.AdTracker)

    override val params = analyticsParams {
        param("item_count", itemCount)
        param("total_cents", totalCents)
        promoCode?.let { param("promo_code", it) }
    }
}
