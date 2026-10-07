package krio.systemdesign.shoppingapp.feature.catalog.impl.analytics

import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.analyticsParams
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem

internal class AddToCartAnalyticsEvent(
    productId: String,
    priceCents: Long,
    screen: AnalyticsScreen,
) : AnalyticsEvent("add_to_cart") {
    override val systems = setOf(AnalyticsSystem.Insights, AnalyticsSystem.AdTracker)

    override val params = analyticsParams {
        param("product_id", productId)
        param("price_cents", priceCents)
        param("screen", screen.value)
    }
}
