package krio.systemdesign.shoppingapp.shared.analytics.system

import krio.systemdesign.shoppingapp.shared.analytics.AnalyticsEvent

interface AnalyticsSystemClient {
    val system: AnalyticsSystem

    fun log(event: AnalyticsEvent)
}
