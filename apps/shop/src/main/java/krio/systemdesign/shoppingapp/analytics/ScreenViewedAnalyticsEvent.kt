package krio.systemdesign.shoppingapp.analytics

import krio.systemdesign.shoppingapp.crossfeature.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.crossfeature.analytics.analyticsParams
import krio.systemdesign.shoppingapp.crossfeature.analytics.event.AnalyticsScreen
import krio.systemdesign.shoppingapp.crossfeature.analytics.system.AnalyticsSystem

internal class ScreenViewedAnalyticsEvent(screen: AnalyticsScreen) : AnalyticsEvent("screen_viewed") {
    override val systems = setOf(AnalyticsSystem.Insights)

    override val params = analyticsParams {
        param("screen", screen.value)
    }
}
