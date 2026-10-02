package krio.systemdesign.shoppingapp.feature.settings.presentation.settings

import krio.systemdesign.shoppingapp.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.domain.model.ThemeMode

data class SettingsUiState(
    // Null — настройка ещё не прочитана.
    val themeMode: ThemeMode? = null,
    // Null — настройка ещё не прочитана.
    val networkDelay: NetworkDelay? = null,
)
