package krio.systemdesign.shoppingapp.analytics

import co.touchlab.kermit.Logger
import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystemClient

// Stands in for an ad analytics system such as AppsFlyer.
internal class FakeAdTrackerClient : AnalyticsSystemClient {
    override val system = AnalyticsSystem.AdTracker

    private val logger = Logger.withTag("Analytics/AdTracker")

    override fun log(event: AnalyticsEvent) {
        logger.i { "$event" }
    }
}
