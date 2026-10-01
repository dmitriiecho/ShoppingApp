package krio.systemdesign.shoppingapp.feature.settings.presentation.settings

import krio.systemdesign.shoppingapp.domain.model.ThemeMode

data class SettingsUiState(
    // Null — настройка ещё не прочитана.
    val themeMode: ThemeMode? = null,
)
