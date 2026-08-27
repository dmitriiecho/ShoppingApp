package krio.systemdesign.shoppingapp.core.network.di

import dagger.Module
import dagger.multibindings.Multibinds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkBindingsModule {
    @Multibinds
    @ApplicationInterceptor
    abstract fun applicationInterceptors(): Set<Interceptor>

    @Multibinds
    @NetworkInterceptor
    abstract fun networkInterceptors(): Set<Interceptor>
}
