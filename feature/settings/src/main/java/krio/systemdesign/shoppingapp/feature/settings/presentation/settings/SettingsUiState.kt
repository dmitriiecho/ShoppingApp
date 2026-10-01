package krio.systemdesign.shoppingapp.feature.settings.presentation.settings

import krio.systemdesign.shoppingapp.domain.model.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.System,
)
