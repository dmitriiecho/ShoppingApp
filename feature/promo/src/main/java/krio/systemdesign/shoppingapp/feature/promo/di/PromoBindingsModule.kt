package krio.systemdesign.shoppingapp.feature.promo.di

import krio.systemdesign.shoppingapp.feature.promo.data.repository.PromoCodeRepositoryImpl
import krio.systemdesign.shoppingapp.feature.promo.domain.repository.PromoCodeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PromoBindingsModule {
    @Binds
    @Singleton
    abstract fun bindPromoCodeRepository(
        impl: PromoCodeRepositoryImpl,
    ): PromoCodeRepository
}
