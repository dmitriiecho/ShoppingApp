package krio.systemdesign.shoppingapp.feature.promo.impl.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import krio.systemdesign.shoppingapp.feature.promo.impl.data.api.PromoApi
import retrofit2.Retrofit
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
internal object PromoModule {
    @Provides
    @Singleton
    fun providePromoApi(retrofit: Retrofit): PromoApi = retrofit.create()
}
