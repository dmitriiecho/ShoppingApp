package krio.systemdesign.shoppingapp.core.network.di

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import krio.systemdesign.shoppingapp.core.config.ServerConfig
import krio.systemdesign.shoppingapp.core.network.networkJsonConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationInterceptor applicationInterceptors: Set<@JvmSuppressWildcards Interceptor>,
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT.toJavaDuration())
        .readTimeout(TIMEOUT.toJavaDuration())
        .writeTimeout(TIMEOUT.toJavaDuration())
        .apply { applicationInterceptors.forEach(::addInterceptor) }
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: Lazy<OkHttpClient>): Retrofit = Retrofit.Builder()
        .baseUrl(ServerConfig.BASE_URL)
        .callFactory { okHttpClient.get().newCall(it) }
        .addConverterFactory(networkJsonConverterFactory)
        .build()

    private val TIMEOUT = 30.seconds
}
