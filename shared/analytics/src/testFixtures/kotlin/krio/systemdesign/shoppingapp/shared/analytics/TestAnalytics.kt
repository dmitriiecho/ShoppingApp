package krio.systemdesign.shoppingapp.shared.analytics

import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem
import krio.systemdesign.shoppingapp.shared.analytics.system.TestAnalyticsClient

// Analytics for a ViewModel test, with a TestAnalyticsClient for every system. A failing client fails the test.
class TestAnalytics {
    private val events = mutableListOf<AnalyticsEvent>()

    val analytics = Analytics(
        clients = AnalyticsSystem.entries.map { TestAnalyticsClient(it, events) }.toSet(),
        onClientError = { _, e -> throw e },
    )

    // Every event in the order it was sent. An event sent to several systems is in events once per system,
    // one after another, so it is kept once.
    val sentEvents: List<SentEvent>
        get() = events.filterIndexed { i, event -> i == 0 || events[i - 1] !== event }.map { it.sent() }
}

// Events have no equals, so tests compare what was sent: the name and the params.
data class SentEvent(
    val name: String,
    val params: Map<String, AnalyticsValue>,
)

fun AnalyticsEvent.sent() = SentEvent(name, params)
