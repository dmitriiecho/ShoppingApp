package krio.systemdesign.shoppingapp.feature.catalog.impl.analytics

import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.analyticsParams
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem

// Only the query's length: the text itself may hold anything the user typed.
internal class ProductsSearchedAnalyticsEvent(queryLength: Int) : AnalyticsEvent("products_searched") {
    override val systems = setOf(AnalyticsSystem.Insights)

    override val params = analyticsParams {
        param("query_length", queryLength)
    }
}
