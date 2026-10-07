package krio.systemdesign.shoppingapp.analytics

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import krio.systemdesign.shoppingapp.shared.analytics.Analytics
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
internal object AnalyticsModule {
    @Provides
    @Singleton
    fun provideAnalytics(): Analytics = Analytics(
        clients = setOf(FakeInsightsClient(), FakeAdTrackerClient()),
        onClientError = { system, error ->
            Timber.e(error, "Analytics system %s failed to log an event", system)
        },
    )
}
