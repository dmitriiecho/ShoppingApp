package krio.systemdesign.shoppingapp.data.di

import krio.systemdesign.shoppingapp.data.repository.CartRepositoryImpl
import krio.systemdesign.shoppingapp.data.source.FakeCartValidatorDataSource
import krio.systemdesign.shoppingapp.data.source.CartValidatorDataSource
import krio.systemdesign.shoppingapp.data.source.LocalCartDataSource
import krio.systemdesign.shoppingapp.data.source.RemoteCartDataSource
import krio.systemdesign.shoppingapp.domain.repository.CartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataBindingsModule {
    @Binds
    @Singleton
    abstract fun bindLocalCartDataSource(
        impl: RemoteCartDataSource,
    ): LocalCartDataSource

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        impl: CartRepositoryImpl,
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindCartValidatorDataSource(
        impl: FakeCartValidatorDataSource,
    ): CartValidatorDataSource
}
