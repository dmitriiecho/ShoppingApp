package krio.systemdesign.shoppingapp.feature.cart.impl.analytics

import krio.systemdesign.shoppingapp.crossfeature.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.crossfeature.analytics.analyticsParams
import krio.systemdesign.shoppingapp.crossfeature.analytics.system.AnalyticsSystem

internal class PromoCodeAppliedAnalyticsEvent(
    code: String,
    discountPercent: Int,
) : AnalyticsEvent("promo_code_applied") {
    override val systems = setOf(AnalyticsSystem.Insights)

    override val params = analyticsParams {
        param("code", code)
        param("discount_percent", discountPercent)
    }
}
