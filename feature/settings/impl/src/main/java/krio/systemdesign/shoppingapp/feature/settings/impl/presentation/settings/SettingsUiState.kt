package krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings

import krio.systemdesign.shoppingapp.shared.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.shared.domain.model.ThemeMode

data class SettingsUiState(val values: Values = Values.Loading) {
    // The saved settings: Loading until they're read.
    sealed interface Values {
        data object Loading : Values

        data class Loaded(
            val themeMode: ThemeMode,
            val networkDelay: NetworkDelay,
        ) : Values
    }
}
