package krio.systemdesign.shoppingapp.feature.promo.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import krio.systemdesign.shoppingapp.feature.promo.impl.data.repository.PromoCodeRepositoryImpl
import krio.systemdesign.shoppingapp.feature.promo.impl.domain.repository.PromoCodeRepository

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PromoBindingsModule {
    @Binds
    @Singleton
    abstract fun bindPromoCodeRepository(impl: PromoCodeRepositoryImpl): PromoCodeRepository
}
