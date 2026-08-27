package krio.systemdesign.shoppingapp.core.config.di

import krio.systemdesign.shoppingapp.core.config.DatabaseSettings
import krio.systemdesign.shoppingapp.core.config.NetworkSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {
    @Provides
    @Singleton
    fun provideNetworkSettings(): NetworkSettings = NetworkSettings(
        baseUrl = "https://api.example.com/",
    )

    @Provides
    @Singleton
    fun provideDatabaseSettings(): DatabaseSettings = DatabaseSettings(
        name = "shopping.db",
    )
}
