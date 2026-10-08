package krio.systemdesign.shoppingapp.shared.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import krio.systemdesign.shoppingapp.shared.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.shared.domain.model.ThemeMode

class AppSettingsRepositoryImplTest {

    @Test
    fun `saved theme is read back`() = runTest {
        val settings = AppSettingsRepositoryImpl(settingsDataStore())

        settings.setThemeMode(ThemeMode.Dark)

        assertThat(settings.observeThemeMode().first()).isEqualTo(ThemeMode.Dark)
    }

    // A value the app no longer knows, e.g. saved by a version with another set of themes.
    @Test
    fun `unknown saved theme falls back to the system's`() = runTest {
        val dataStore = settingsDataStore()
        dataStore.edit { it[stringPreferencesKey("theme_mode")] = "Sepia" }

        val themeMode = AppSettingsRepositoryImpl(dataStore).observeThemeMode().first()

        assertThat(themeMode).isEqualTo(ThemeMode.System)
    }

    @Test
    fun `saved network delay is read back`() = runTest {
        val settings = AppSettingsRepositoryImpl(settingsDataStore())

        settings.setNetworkDelay(NetworkDelay.TwoSeconds)

        assertThat(settings.observeNetworkDelay().first()).isEqualTo(NetworkDelay.TwoSeconds)
    }

    @Test
    fun `unknown saved network delay falls back to none`() = runTest {
        val dataStore = settingsDataStore()
        dataStore.edit { it[stringPreferencesKey("network_delay")] = "TenSeconds" }

        val networkDelay = AppSettingsRepositoryImpl(dataStore).observeNetworkDelay().first()

        assertThat(networkDelay).isEqualTo(NetworkDelay.None)
    }

    // A real DataStore in a fresh file, as the app has, but without an Android Context.
    private fun TestScope.settingsDataStore(): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = backgroundScope,
        produceFile = { createTempDirectory("settings").resolve("settings.preferences_pb").toFile() },
    )
}
