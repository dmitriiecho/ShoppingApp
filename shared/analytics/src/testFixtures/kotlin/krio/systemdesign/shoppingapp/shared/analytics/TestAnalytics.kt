package krio.systemdesign.shoppingapp.shared.analytics

import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem
import krio.systemdesign.shoppingapp.shared.analytics.system.TestAnalyticsClient

// Analytics for a ViewModel test, with a TestAnalyticsClient for every system. A failing client fails the test.
class TestAnalytics {
    private val clients = AnalyticsSystem.entries.map(::TestAnalyticsClient)

    val analytics = Analytics(clients.toSet(), onClientError = { _, e -> throw e })

    // Every event once, even when it went to several systems.
    val sentEvents: List<SentEvent>
        get() = clients.flatMap { it.events }.distinct().map { it.sent() }
}

// Events have no equals, so tests compare what was sent: the name and the params.
data class SentEvent(
    val name: String,
    val params: Map<String, AnalyticsValue>,
)

fun AnalyticsEvent.sent() = SentEvent(name, params)
