package krio.systemdesign.shoppingapp.analytics

import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystemClient
import timber.log.Timber

// Stands in for a product analytics system such as Firebase Analytics.
internal class FakeInsightsClient : AnalyticsSystemClient {
    override val system = AnalyticsSystem.Insights

    override fun log(event: AnalyticsEvent) {
        Timber.tag("Analytics/Insights").i("%s", event)
    }
}
