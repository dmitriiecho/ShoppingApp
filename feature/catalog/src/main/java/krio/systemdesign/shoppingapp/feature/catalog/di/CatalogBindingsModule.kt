package krio.systemdesign.shoppingapp.feature.catalog.di

import krio.systemdesign.shoppingapp.feature.catalog.data.repository.ProductsRepositoryImpl
import krio.systemdesign.shoppingapp.feature.catalog.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CatalogBindingsModule {
    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: ProductsRepositoryImpl,
    ): ProductRepository
}
