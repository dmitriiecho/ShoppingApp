package krio.systemdesign.shoppingapp.shared.analytics.system

import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent

// Keeps the events sent to its system; with error set, fails every log as a broken analytics SDK would.
class TestAnalyticsClient(override val system: AnalyticsSystem) : AnalyticsSystemClient {
    val events = mutableListOf<AnalyticsEvent>()
    var error: Exception? = null

    override fun log(event: AnalyticsEvent) {
        error?.let { throw it }
        events += event
    }
}
