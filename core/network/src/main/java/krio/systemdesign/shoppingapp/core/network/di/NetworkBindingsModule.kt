package krio.systemdesign.shoppingapp.core.network.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import okhttp3.Interceptor

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkBindingsModule {
    // The set is never empty now (NetworkDelayInterceptor),
    // kept @Multibinds in case it becomes empty: Dagger fails on that.
    @Multibinds
    @ApplicationInterceptor
    abstract fun applicationInterceptors(): Set<Interceptor>
}
