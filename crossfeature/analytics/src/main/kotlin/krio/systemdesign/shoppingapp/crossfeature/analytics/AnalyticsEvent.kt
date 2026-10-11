package krio.systemdesign.shoppingapp.crossfeature.analytics

import krio.systemdesign.shoppingapp.crossfeature.analytics.system.AnalyticsSystem

abstract class AnalyticsEvent(val name: String) {
    abstract val systems: Set<AnalyticsSystem>
    abstract val params: Map<String, AnalyticsValue>

    override fun toString() = "$name $params"
}
