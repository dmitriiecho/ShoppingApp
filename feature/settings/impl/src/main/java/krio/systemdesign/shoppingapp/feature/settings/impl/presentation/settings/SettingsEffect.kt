package krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings

import krio.systemdesign.shoppingapp.core.composeutils.text.UiText

internal sealed interface SettingsEffect {

    data class OpenUrl(val url: UiText) : SettingsEffect

    data class ShowSnackBar(val message: UiText) : SettingsEffect

    // Never sent in a tab. Needed when the screen is embedded in a flow the user can leave.
    data object NavigateBack : SettingsEffect
}
