package krio.systemdesign.shoppingapp.crossfeature.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import krio.systemdesign.shoppingapp.crossfeature.data.database.ShoppingDatabase
import krio.systemdesign.shoppingapp.crossfeature.data.database.dao.AppliedPromoCodeDao
import krio.systemdesign.shoppingapp.crossfeature.data.database.dao.CartItemDao
import okio.Path.Companion.toPath

// Public: the app's graph in :apps:shop skips an internal binding container. The functions that give
// the module's own types stay internal. Where the files live is up to each platform (AndroidDataModule,
// IosDataModule).
@BindingContainer
@ContributesTo(AppScope::class)
object DataModule {
    @Provides
    @SingleIn(AppScope::class)
    internal fun provideDatabase(builder: RoomDatabase.Builder<ShoppingDatabase>): ShoppingDatabase = builder
        // The app's own SQLite, the same on Android and iOS, instead of each system's version.
        .setDriver(BundledSQLiteDriver())
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
    fun provideSettingsDataStore(file: SettingsFile): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
            produceFile = { file.path.toPath() },
        )
}

// The full path of the settings file, given by each platform.
class SettingsFile(val path: String)

internal const val DATABASE_NAME = "shopping.db"
