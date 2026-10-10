package krio.systemdesign.shoppingapp.analytics

import co.touchlab.kermit.Logger
import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystemClient

// Stands in for a product analytics system such as Firebase Analytics.
internal class FakeInsightsClient : AnalyticsSystemClient {
    override val system = AnalyticsSystem.Insights

    private val logger = Logger.withTag("Analytics/Insights")

    override fun log(event: AnalyticsEvent) {
        logger.i { "$event" }
    }
}
