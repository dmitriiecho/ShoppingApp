package krio.systemdesign.shoppingapp.analytics

import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystemClient
import timber.log.Timber

// Stands in for an ad analytics system such as AppsFlyer.
internal class FakeAdTrackerClient : AnalyticsSystemClient {
    override val system = AnalyticsSystem.AdTracker

    override fun log(event: AnalyticsEvent) {
        Timber.tag("Analytics/AdTracker").i("%s", event)
    }
}
