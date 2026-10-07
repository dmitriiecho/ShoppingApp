package krio.systemdesign.shoppingapp.shared.analytics.event.cart

import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.analyticsParams
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem

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
