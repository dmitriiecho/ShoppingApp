package krio.systemdesign.shoppingapp.shared.domain.repository

import kotlinx.coroutines.flow.Flow
import krio.systemdesign.shoppingapp.shared.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.shared.domain.model.ThemeMode

interface AppSettingsRepository {

    fun observeThemeMode(): Flow<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode): Result<Unit>

    fun observeNetworkDelay(): Flow<NetworkDelay>

    suspend fun setNetworkDelay(delay: NetworkDelay): Result<Unit>
}
