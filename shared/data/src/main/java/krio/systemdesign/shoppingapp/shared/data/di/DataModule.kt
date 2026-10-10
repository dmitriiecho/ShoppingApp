package krio.systemdesign.shoppingapp.shared.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import krio.systemdesign.shoppingapp.shared.data.database.ShoppingDatabase
import krio.systemdesign.shoppingapp.shared.data.database.dao.AppliedPromoCodeDao
import krio.systemdesign.shoppingapp.shared.data.database.dao.CartItemDao

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

    // One instance per file: DataStore requires it. A corrupted file is replaced with an empty one, the settings
    // back to their defaults: otherwise every later save would fail on reading it.
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            produceFile = { context.preferencesDataStoreFile(SETTINGS_FILE_NAME) },
        )

    private const val DATABASE_NAME = "shopping.db"
    private const val SETTINGS_FILE_NAME = "settings"
}
