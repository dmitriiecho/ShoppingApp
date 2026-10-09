package krio.systemdesign.shoppingapp.shared.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import krio.systemdesign.shoppingapp.shared.data.api.CartApi
import krio.systemdesign.shoppingapp.shared.data.database.ShoppingDatabase
import krio.systemdesign.shoppingapp.shared.data.database.dao.AppliedPromoCodeDao
import krio.systemdesign.shoppingapp.shared.data.database.dao.CartItemDao
import retrofit2.Retrofit
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ShoppingDatabase =
        Room.databaseBuilder(context, ShoppingDatabase::class.java, DATABASE_NAME)
            .setQueryCoroutineContext(Dispatchers.IO)
            // No migrations before the first release: a new database version recreates the database, losing
            // the cart. Room does it only when the version changes, so every schema change bumps it.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideCartItemDao(database: ShoppingDatabase): CartItemDao = database.cartItemDao()

    @Provides
    fun provideAppliedPromoCodeDao(database: ShoppingDatabase): AppliedPromoCodeDao = database.appliedPromoCodeDao()

    @Provides
    @Singleton
    fun provideCartApi(retrofit: Retrofit): CartApi = retrofit.create()

    // One instance per file: DataStore requires it.
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(SETTINGS_FILE_NAME) }

    private const val DATABASE_NAME = "shopping.db"
    private const val SETTINGS_FILE_NAME = "settings"
}
