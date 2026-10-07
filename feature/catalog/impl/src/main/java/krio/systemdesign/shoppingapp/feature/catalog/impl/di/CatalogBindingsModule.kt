package krio.systemdesign.shoppingapp.feature.catalog.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import krio.systemdesign.shoppingapp.feature.catalog.impl.data.repository.ProductRepositoryImpl
import krio.systemdesign.shoppingapp.feature.catalog.impl.domain.repository.ProductRepository

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CatalogBindingsModule {
    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository
}
