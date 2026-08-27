package krio.systemdesign.shoppingapp.feature.catalog.di

import krio.systemdesign.shoppingapp.feature.catalog.BuildConfig
import krio.systemdesign.shoppingapp.feature.catalog.data.api.FakeProductsApi
import krio.systemdesign.shoppingapp.feature.catalog.data.api.ProductsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CatalogModule {
    @Provides
    @Singleton
    fun provideProductsApi(retrofit: Retrofit): ProductsApi =
        if (BuildConfig.DEBUG) FakeProductsApi() else retrofit.create()
}
