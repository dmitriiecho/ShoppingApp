package krio.systemdesign.shoppingapp.core.network.di

import krio.systemdesign.shoppingapp.core.config.NetworkSettings
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton
import kotlin.time.toJavaDuration

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        settings: NetworkSettings,
        @ApplicationInterceptor applicationInterceptors: Set<@JvmSuppressWildcards Interceptor>,
        @NetworkInterceptor networkInterceptors: Set<@JvmSuppressWildcards Interceptor>,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(settings.connectTimeout.toJavaDuration())
            .readTimeout(settings.readTimeout.toJavaDuration())
            .writeTimeout(settings.writeTimeout.toJavaDuration())
            .apply {
                applicationInterceptors.forEach(::addInterceptor)
                networkInterceptors.forEach(::addNetworkInterceptor)
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        settings: NetworkSettings,
        json: Json,
        okHttpClient: Lazy<OkHttpClient>,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(settings.baseUrl)
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(json.asConverterFactory(JSON_MEDIA_TYPE))
            .build()
    }

    private val JSON_MEDIA_TYPE = "application/json; charset=UTF-8".toMediaType()
}
