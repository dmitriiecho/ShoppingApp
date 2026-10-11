package krio.systemdesign.shoppingapp.shared.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers
import krio.systemdesign.shoppingapp.shared.data.database.ShoppingDatabase
import krio.systemdesign.shoppingapp.shared.data.database.dao.AppliedPromoCodeDao
import krio.systemdesign.shoppingapp.shared.data.database.dao.CartItemDao

// Public: the app's graph in :apps:shop skips an internal binding container. The functions that give
// the module's own types stay internal.
@BindingContainer
@ContributesTo(AppScope::class)
object DataModule {
    @Provides
    @SingleIn(AppScope::class)
    internal fun provideDatabase(context: Context): ShoppingDatabase =
        Room.databaseBuilder(context, ShoppingDatabase::class.java, DATABASE_NAME)
            .setQueryCoroutineContext(Dispatchers.IO)
            // No migrations before the first release: a new database version recreates the database, losing
            // the cart. Room does it only when the version changes, so every schema change bumps it.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    internal fun provideCartItemDao(database: ShoppingDatabase): CartItemDao = database.cartItemDao()

    @Provides
    internal fun provideAppliedPromoCodeDao(database: ShoppingDatabase): AppliedPromoCodeDao =
        database.appliedPromoCodeDao()

    // One instance per file: DataStore requires it. A corrupted file is replaced with an empty one, the settings
    // back to their defaults: otherwise every later save would fail on reading it.
    @Provides
    @SingleIn(AppScope::class)
    fun provideSettingsDataStore(context: Context): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = { context.preferencesDataStoreFile(SETTINGS_FILE_NAME) },
    )

    private const val DATABASE_NAME = "shopping.db"
    private const val SETTINGS_FILE_NAME = "settings"
}
