package krio.systemdesign.shoppingapp.analytics

import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent
import krio.systemdesign.shoppingapp.shared.analytics.analyticsParams
import krio.systemdesign.shoppingapp.shared.analytics.event.AnalyticsScreen
import krio.systemdesign.shoppingapp.shared.analytics.system.AnalyticsSystem

internal class ScreenViewedAnalyticsEvent(screen: AnalyticsScreen) : AnalyticsEvent("screen_viewed") {
    override val systems = setOf(AnalyticsSystem.Insights)

    override val params = analyticsParams {
        param("screen", screen.value)
    }
}
