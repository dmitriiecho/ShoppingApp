package krio.systemdesign.shoppingapp.feature.promo.impl.analytics

import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.analyticsParams
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem

internal class PromoCodeRejectedAnalyticsEvent(code: String) : AnalyticsEvent("promo_code_rejected") {
    override val systems = setOf(AnalyticsSystem.Insights)

    override val params = analyticsParams {
        param("code", code)
    }
}
