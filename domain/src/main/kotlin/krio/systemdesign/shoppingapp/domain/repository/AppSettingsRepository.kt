package krio.systemdesign.shoppingapp.domain.repository

import krio.systemdesign.shoppingapp.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

// Настройки приложения, которые выбирает пользователь и которые сохраняются между запусками.
interface AppSettingsRepository {

    fun observeThemeMode(): Flow<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode): Result<Unit>

    fun observeNetworkDelay(): Flow<NetworkDelay>

    suspend fun setNetworkDelay(delay: NetworkDelay): Result<Unit>
}
