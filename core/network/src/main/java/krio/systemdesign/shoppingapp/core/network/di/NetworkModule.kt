package krio.systemdesign.shoppingapp.core.network.di

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import kotlinx.serialization.json.Json
import krio.systemdesign.shoppingapp.core.config.ServerConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

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
        @ApplicationInterceptor applicationInterceptors: Set<@JvmSuppressWildcards Interceptor>,
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT.toJavaDuration())
        .readTimeout(TIMEOUT.toJavaDuration())
        .writeTimeout(TIMEOUT.toJavaDuration())
        .apply { applicationInterceptors.forEach(::addInterceptor) }
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        json: Json,
        okHttpClient: Lazy<OkHttpClient>,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(ServerConfig.BASE_URL)
        .callFactory { okHttpClient.get().newCall(it) }
        .addConverterFactory(json.asConverterFactory(JSON_MEDIA_TYPE))
        .build()

    private val JSON_MEDIA_TYPE = "application/json; charset=UTF-8".toMediaType()
    private val TIMEOUT = 30.seconds
}
