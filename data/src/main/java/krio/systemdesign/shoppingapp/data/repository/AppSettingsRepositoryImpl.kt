package krio.systemdesign.shoppingapp.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import krio.systemdesign.shoppingapp.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class AppSettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : AppSettingsRepository {

    // Файл настроек не прочитался — берём значения по умолчанию, а не роняем приложение.
    private val preferences: Flow<Preferences> = dataStore.data
        .catch { e ->
            if (e !is IOException) throw e
            Log.w(TAG, "Не удалось прочитать настройки", e)
            emit(emptyPreferences())
        }

    override fun observeThemeMode(): Flow<ThemeMode> = preferences
        .map { preferences ->
            // Тема хранится по имени: если сохранённого значения нет или оно незнакомое, берём системную.
            val name = preferences[THEME_MODE_KEY]
            ThemeMode.entries.find { it.name == name } ?: ThemeMode.System
        }

    override suspend fun setThemeMode(mode: ThemeMode): Result<Unit> =
        try {
            dataStore.edit { it[THEME_MODE_KEY] = mode.name }
            Result.success(Unit)
        } catch (e: IOException) {
            Log.w(TAG, "Не удалось сохранить тему", e)
            Result.failure(e)
        }

    override fun observeNetworkDelay(): Flow<NetworkDelay> = preferences
        .map { preferences ->
            // Как и тема, хранится по имени. Нет значения или оно незнакомое — без задержки.
            val name = preferences[NETWORK_DELAY_KEY]
            NetworkDelay.entries.find { it.name == name } ?: NetworkDelay.None
        }

    override suspend fun setNetworkDelay(delay: NetworkDelay): Result<Unit> =
        try {
            dataStore.edit { it[NETWORK_DELAY_KEY] = delay.name }
            Result.success(Unit)
        } catch (e: IOException) {
            Log.w(TAG, "Не удалось сохранить задержку запросов", e)
            Result.failure(e)
        }

    private companion object {
        const val TAG = "AppSettingsRepository"
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val NETWORK_DELAY_KEY = stringPreferencesKey("network_delay")
    }
}
