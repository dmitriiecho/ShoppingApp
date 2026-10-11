package krio.systemdesign.shoppingapp.crossfeature.analytics.system

import krio.systemdesign.shoppingapp.crossfeature.analytics.AnalyticsEvent

interface AnalyticsSystemClient {
    val system: AnalyticsSystem

    fun log(event: AnalyticsEvent)
}
