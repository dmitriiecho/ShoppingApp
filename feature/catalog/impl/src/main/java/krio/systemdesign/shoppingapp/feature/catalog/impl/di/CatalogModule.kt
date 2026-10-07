package krio.systemdesign.shoppingapp.feature.catalog.impl.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import krio.systemdesign.shoppingapp.feature.catalog.impl.data.api.ProductApi
import retrofit2.Retrofit
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
internal object CatalogModule {
    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi = retrofit.create()
}
