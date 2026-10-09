package krio.systemdesign.shoppingapp.shared.analytics.system

import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent

// Keeps the events sent to its system; with error set, fails every log as a broken analytics SDK would.
// Clients given one events list keep the events of all their systems there, in the order they were sent.
class TestAnalyticsClient(
    override val system: AnalyticsSystem,
    val events: MutableList<AnalyticsEvent> = mutableListOf(),
) : AnalyticsSystemClient {
    var error: Exception? = null

    override fun log(event: AnalyticsEvent) {
        error?.let { throw it }
        events += event
    }
}
