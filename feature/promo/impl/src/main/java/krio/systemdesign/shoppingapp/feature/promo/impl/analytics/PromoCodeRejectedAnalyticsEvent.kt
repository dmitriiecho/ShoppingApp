package krio.systemdesign.shoppingapp.feature.promo.impl.analytics

import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.analyticsParams
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem

// The code as typed, unlike the search query (ProductsSearchedAnalyticsEvent): the field is meant for short codes,
// and which unknown codes people try (old campaigns, typos of real ones) is what this event is for.
internal class PromoCodeRejectedAnalyticsEvent(code: String) : AnalyticsEvent("promo_code_rejected") {
    override val systems = setOf(AnalyticsSystem.Insights)

    override val params = analyticsParams {
        param("code", code)
    }
}
