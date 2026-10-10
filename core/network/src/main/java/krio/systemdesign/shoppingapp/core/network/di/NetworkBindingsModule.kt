package krio.systemdesign.shoppingapp.core.network.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import krio.systemdesign.shoppingapp.core.network.HttpClientSetup

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkBindingsModule {
    // The set is never empty now (NetworkDelayPlugin),
    // kept @Multibinds in case it becomes empty: Dagger fails on that.
    @Multibinds
    abstract fun httpClientSetups(): Set<HttpClientSetup>
}
