package krio.systemdesign.shoppingapp.crossfeature.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import krio.systemdesign.shoppingapp.crossfeature.data.database.ShoppingDatabase

// Where the database and the settings live on Android: the app's private files, the same as before the move to KMP,
// so an update keeps the cart and the settings.
@BindingContainer
@ContributesTo(AppScope::class)
object AndroidDataModule {
    @Provides
    internal fun provideDatabaseBuilder(context: Context): RoomDatabase.Builder<ShoppingDatabase> {
        val appContext = context.applicationContext
        return Room.databaseBuilder<ShoppingDatabase>(
            context = appContext,
            name = appContext.getDatabasePath(DATABASE_NAME).absolutePath,
        )
    }

    // The file DataStore's preferencesDataStoreFile("settings") used.
    @Provides
    fun provideSettingsFile(context: Context): SettingsFile =
        SettingsFile(context.applicationContext.filesDir.resolve("datastore/settings.preferences_pb").absolutePath)
}
