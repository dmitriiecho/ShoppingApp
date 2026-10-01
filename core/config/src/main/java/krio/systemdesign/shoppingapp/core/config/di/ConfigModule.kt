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
        // Наш сервер (исходники в server/). HTTPS у него нет, поэтому HTTP для этого адреса разрешён
        // в app/src/main/res/xml/network_security_config.xml: при смене адреса поменяйте его и там.
        baseUrl = "http://2.56.204.151:8080/",
    )

    @Provides
    @Singleton
    fun provideDatabaseSettings(): DatabaseSettings = DatabaseSettings(
        name = "shopping.db",
    )
}
