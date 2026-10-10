package krio.systemdesign.shoppingapp.shared.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import co.touchlab.kermit.Logger
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import krio.systemdesign.shoppingapp.shared.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.shared.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.shared.domain.repository.AppSettingsRepository

// ktlint would wrap this long header before the supertype; the project puts one parameter per line instead.
@Suppress("ktlint:standard:class-signature")
internal class AppSettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : AppSettingsRepository {

    private val preferences: Flow<Preferences> = dataStore.data
        .catch { e ->
            if (e !is IOException) throw e
            Logger.w(e) { "Failed to read settings" }
            emit(emptyPreferences())
        }

    override fun observeThemeMode(): Flow<ThemeMode> = preferences
        .map { preferences ->
            val name = preferences[THEME_MODE_KEY]
            ThemeMode.entries.find { it.name == name } ?: ThemeMode.System
        }

    override suspend fun setThemeMode(mode: ThemeMode): Result<Unit> = try {
        dataStore.edit { it[THEME_MODE_KEY] = mode.name }
        Result.success(Unit)
    } catch (e: IOException) {
        Logger.e(e) { "Failed to save theme" }
        Result.failure(e)
    }

    override fun observeNetworkDelay(): Flow<NetworkDelay> = preferences
        .map { preferences ->
            val name = preferences[NETWORK_DELAY_KEY]
            NetworkDelay.entries.find { it.name == name } ?: NetworkDelay.None
        }

    override suspend fun setNetworkDelay(delay: NetworkDelay): Result<Unit> = try {
        dataStore.edit { it[NETWORK_DELAY_KEY] = delay.name }
        Result.success(Unit)
    } catch (e: IOException) {
        Logger.e(e) { "Failed to save network delay" }
        Result.failure(e)
    }

    private companion object {
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val NETWORK_DELAY_KEY = stringPreferencesKey("network_delay")
    }
}
