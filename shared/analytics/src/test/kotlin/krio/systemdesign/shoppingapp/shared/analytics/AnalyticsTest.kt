package krio.systemdesign.shoppingapp.shared.analytics

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isInstanceOf
import kotlin.test.Test
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem
import krio.systemdesign.shoppingapp.shared.analytics.system.TestAnalyticsClient

class AnalyticsTest {

    private val insights = TestAnalyticsClient(AnalyticsSystem.Insights)
    private val adTracker = TestAnalyticsClient(AnalyticsSystem.AdTracker)

    @Test
    fun `analytics without a client for every system is rejected`() {
        assertFailure { Analytics(setOf(insights), onClientError = { _, _ -> }) }
            .isInstanceOf<IllegalArgumentException>()
    }

    @Test
    fun `analytics with two clients for one system is rejected`() {
        val secondInsights = TestAnalyticsClient(AnalyticsSystem.Insights)

        assertFailure { Analytics(setOf(insights, secondInsights, adTracker), onClientError = { _, _ -> }) }
            .isInstanceOf<IllegalArgumentException>()
    }

    @Test
    fun `event reaches only the systems it names`() {
        val analytics = Analytics(setOf(insights, adTracker), onClientError = { _, _ -> })
        val event = TestAnalyticsEvent(AnalyticsSystem.Insights)

        analytics.log(event)

        assertThat(insights.events).containsExactly(event)
        assertThat(adTracker.events).isEmpty()
    }

    @Test
    fun `failing client does not keep the event from the other systems`() {
        val analytics = Analytics(setOf(insights, adTracker), onClientError = { _, _ -> })
        insights.error = IllegalStateException("SDK is down")
        val event = TestAnalyticsEvent(AnalyticsSystem.Insights, AnalyticsSystem.AdTracker)

        analytics.log(event)

        assertThat(adTracker.events).containsExactly(event)
    }

    @Test
    fun `failing client's error is passed to onClientError with its system`() {
        val errors = mutableListOf<Pair<AnalyticsSystem, Throwable>>()
        val analytics = Analytics(setOf(insights, adTracker), onClientError = { system, e -> errors += system to e })
        val error = IllegalStateException("SDK is down")
        insights.error = error

        analytics.log(TestAnalyticsEvent(AnalyticsSystem.Insights))

        assertThat(errors).containsExactly(AnalyticsSystem.Insights to error)
    }

    private class TestAnalyticsEvent(vararg systems: AnalyticsSystem) : AnalyticsEvent("test") {
        override val systems = systems.toSet()
        override val params = emptyMap<String, AnalyticsValue>()
    }
}
