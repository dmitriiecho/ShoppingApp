package krio.systemdesign.shoppingapp.analytics

import co.touchlab.kermit.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import krio.systemdesign.shoppingapp.shared.analytics.Analytics

@Module
@InstallIn(SingletonComponent::class)
internal object AnalyticsModule {
    @Provides
    @Singleton
    fun provideAnalytics(): Analytics = Analytics(
        clients = setOf(FakeInsightsClient(), FakeAdTrackerClient()),
        onClientError = { system, error ->
            Logger.e(error) { "Analytics system $system failed to log an event" }
        },
    )
}
