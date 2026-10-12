package krio.systemdesign.shoppingapp.feature.catalog.impl.analytics

import krio.systemdesign.shoppingapp.crossfeature.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.crossfeature.analytics.analyticsParams
import krio.systemdesign.shoppingapp.crossfeature.analytics.system.AnalyticsSystem

internal class ProductViewedAnalyticsEvent(
    productId: String,
    priceCents: Long,
) : AnalyticsEvent("product_viewed") {
    override val systems = setOf(AnalyticsSystem.Insights, AnalyticsSystem.AdTracker)

    override val params = analyticsParams {
        param("product_id", productId)
        param("price_cents", priceCents)
    }
}
