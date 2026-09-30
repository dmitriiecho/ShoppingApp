package krio.systemdesign.shoppingapp.feature.promo.di

import krio.systemdesign.shoppingapp.feature.promo.BuildConfig
import krio.systemdesign.shoppingapp.feature.promo.data.api.FakePromoApi
import krio.systemdesign.shoppingapp.feature.promo.data.api.PromoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object PromoModule {
    @Provides
    @Singleton
    fun providePromoApi(retrofit: Retrofit): PromoApi =
        if (BuildConfig.DEBUG) FakePromoApi() else retrofit.create()
}
