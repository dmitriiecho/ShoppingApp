package krio.systemdesign.shoppingapp.crossfeature.analytics.event.cart

import krio.systemdesign.shoppingapp.crossfeature.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.crossfeature.analytics.analyticsParams
import krio.systemdesign.shoppingapp.crossfeature.analytics.event.AnalyticsScreen
import krio.systemdesign.shoppingapp.crossfeature.analytics.system.AnalyticsSystem

class CartQuantityChangedAnalyticsEvent(
    productId: String,
    quantity: Int,
    screen: AnalyticsScreen,
) : AnalyticsEvent("cart_quantity_changed") {
    override val systems = setOf(AnalyticsSystem.Insights)

    override val params = analyticsParams {
        param("product_id", productId)
        param("quantity", quantity)
        param("screen", screen.value)
    }
}
