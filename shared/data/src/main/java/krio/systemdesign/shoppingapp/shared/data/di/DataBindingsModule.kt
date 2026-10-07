package krio.systemdesign.shoppingapp.shared.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton
import krio.systemdesign.shoppingapp.core.network.di.ApplicationInterceptor
import krio.systemdesign.shoppingapp.shared.data.network.NetworkDelayInterceptor
import krio.systemdesign.shoppingapp.shared.data.repository.AppSettingsRepositoryImpl
import krio.systemdesign.shoppingapp.shared.data.repository.CartRepositoryImpl
import krio.systemdesign.shoppingapp.shared.data.source.CartValidatorDataSource
import krio.systemdesign.shoppingapp.shared.data.source.LocalCartDataSource
import krio.systemdesign.shoppingapp.shared.data.source.NetworkCartValidatorDataSource
import krio.systemdesign.shoppingapp.shared.data.source.RoomLocalCartDataSource
import krio.systemdesign.shoppingapp.shared.domain.repository.AppSettingsRepository
import krio.systemdesign.shoppingapp.shared.domain.repository.CartRepository
import okhttp3.Interceptor

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataBindingsModule {
    @Binds
    @Singleton
    abstract fun bindLocalCartDataSource(impl: RoomLocalCartDataSource): LocalCartDataSource

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindCartValidatorDataSource(impl: NetworkCartValidatorDataSource): CartValidatorDataSource

    @Binds
    @Singleton
    abstract fun bindAppSettingsRepository(impl: AppSettingsRepositoryImpl): AppSettingsRepository

    @Binds
    @IntoSet
    @ApplicationInterceptor
    abstract fun bindNetworkDelayInterceptor(impl: NetworkDelayInterceptor): Interceptor
}
