package krio.systemdesign.shoppingapp.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
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

    override fun observeThemeMode(): Flow<ThemeMode> = dataStore.data
        // Файл настроек не прочитался — показываем тему как в системе, а не роняем приложение.
        .catch { e ->
            if (e !is IOException) throw e
            Log.w(TAG, "Не удалось прочитать настройки", e)
            emit(emptyPreferences())
        }
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

    private companion object {
        const val TAG = "AppSettingsRepository"
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }
}
