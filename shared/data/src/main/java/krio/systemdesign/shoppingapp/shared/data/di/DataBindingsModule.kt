package krio.systemdesign.shoppingapp.shared.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton
import krio.systemdesign.shoppingapp.core.network.HttpClientSetup
import krio.systemdesign.shoppingapp.shared.data.network.NetworkDelayPlugin
import krio.systemdesign.shoppingapp.shared.data.repository.AppSettingsRepositoryImpl
import krio.systemdesign.shoppingapp.shared.data.repository.CartRepositoryImpl
import krio.systemdesign.shoppingapp.shared.domain.repository.AppSettingsRepository
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataBindingsModule {
    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindAppSettingsRepository(impl: AppSettingsRepositoryImpl): AppSettingsRepository

    @Binds
    @IntoSet
    abstract fun bindNetworkDelayPlugin(impl: NetworkDelayPlugin): HttpClientSetup
}
