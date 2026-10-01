package krio.systemdesign.shoppingapp.data.di

import android.content.Context
import androidx.room.Room
import krio.systemdesign.shoppingapp.core.config.DatabaseSettings
import krio.systemdesign.shoppingapp.data.BuildConfig
import krio.systemdesign.shoppingapp.data.api.CartApi
import krio.systemdesign.shoppingapp.data.database.ShoppingDatabase
import krio.systemdesign.shoppingapp.data.database.dao.AppliedPromoCodeDao
import krio.systemdesign.shoppingapp.data.database.dao.CartItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        settings: DatabaseSettings,
    ): ShoppingDatabase {
        return Room.databaseBuilder(
            context,
            ShoppingDatabase::class.java,
            settings.name,
        )
            .setQueryCoroutineContext(Dispatchers.IO)
            .apply {
                if (BuildConfig.DEBUG) {
                    fallbackToDestructiveMigration(dropAllTables = true)
                }
            }
            .build()
    }

    @Provides
    fun provideCartItemDao(database: ShoppingDatabase): CartItemDao {
        return database.cartItemDao()
    }

    @Provides
    fun provideAppliedPromoCodeDao(database: ShoppingDatabase): AppliedPromoCodeDao {
        return database.appliedPromoCodeDao()
    }

    @Provides
    @Singleton
    fun provideCartApi(retrofit: Retrofit): CartApi = retrofit.create()
}
