package krio.systemdesign.shoppingapp.shared.analytics

import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystemClient

class Analytics(
    private val clients: Set<AnalyticsSystemClient>,
    private val onClientError: (AnalyticsSystem, Throwable) -> Unit,
) {

    init {
        // Without this, events for a system with no client would be lost silently, and with two sent twice.
        val systems = clients.map { it.system }
        require(systems.sorted() == AnalyticsSystem.entries) {
            "Each analytics system needs exactly one client, got $systems"
        }
    }

    fun log(event: AnalyticsEvent) {
        for (client in clients.filter { it.system in event.systems }) {
            try {
                client.log(event)
            } catch (e: Exception) {
                // One failing system must not keep the event from the others.
                onClientError(client.system, e)
            }
        }
    }
}
