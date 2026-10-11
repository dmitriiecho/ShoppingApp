package krio.systemdesign.shoppingapp.analytics

import co.touchlab.kermit.Logger
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import krio.systemdesign.shoppingapp.shared.analytics.Analytics

// Public: Metro rejects an internal binding container (nonPublicContributionSeverity in MetroConventionPlugin).
@BindingContainer
@ContributesTo(AppScope::class)
object AnalyticsModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provideAnalytics(): Analytics = Analytics(
        clients = setOf(FakeInsightsClient(), FakeAdTrackerClient()),
        onClientError = { system, error ->
            Logger.e(error) { "Analytics system $system failed to log an event" }
        },
    )
}
