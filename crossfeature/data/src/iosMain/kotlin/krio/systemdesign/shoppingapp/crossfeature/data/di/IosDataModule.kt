package krio.systemdesign.shoppingapp.crossfeature.data.di

import androidx.room.Room
import androidx.room.RoomDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import kotlinx.cinterop.ExperimentalForeignApi
import krio.systemdesign.shoppingapp.crossfeature.data.database.ShoppingDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

// Where the database and the settings live on iOS: the app's Documents folder.
@BindingContainer
@ContributesTo(AppScope::class)
object IosDataModule {
    @Provides
    internal fun provideDatabaseBuilder(): RoomDatabase.Builder<ShoppingDatabase> =
        Room.databaseBuilder<ShoppingDatabase>(name = "${documentsPath()}/$DATABASE_NAME")

    @Provides
    fun provideSettingsFile(): SettingsFile = SettingsFile("${documentsPath()}/settings.preferences_pb")

    @OptIn(ExperimentalForeignApi::class)
    private fun documentsPath(): String = requireNotNull(
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null,
        )?.path,
    )
}
