package krio.systemdesign.shoppingapp.crossfeature.analytics.event.cart

import krio.systemdesign.shoppingapp.crossfeature.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.crossfeature.analytics.analyticsParams
import krio.systemdesign.shoppingapp.crossfeature.analytics.event.AnalyticsScreen
import krio.systemdesign.shoppingapp.crossfeature.analytics.system.AnalyticsSystem

class RemoveFromCartAnalyticsEvent(
    productId: String,
    screen: AnalyticsScreen,
) : AnalyticsEvent("remove_from_cart") {
    override val systems = setOf(AnalyticsSystem.Insights)

    override val params = analyticsParams {
        param("product_id", productId)
        param("screen", screen.value)
    }
}
