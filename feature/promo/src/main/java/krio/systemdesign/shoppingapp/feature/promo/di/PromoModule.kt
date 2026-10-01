package krio.systemdesign.shoppingapp.feature.promo.di

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
    fun providePromoApi(retrofit: Retrofit): PromoApi = retrofit.create()
}
