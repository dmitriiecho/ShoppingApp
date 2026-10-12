package krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings

import krio.systemdesign.shoppingapp.core.composeutils.text.UiText

internal sealed interface SettingsEffect {

    // The page in the screen's language: its address is a string resource the screen reads.
    data object OpenDeepLinksPage : SettingsEffect

    data class ShowSnackBar(val message: UiText) : SettingsEffect

    // Never sent in a tab. Needed when the screen is embedded in a flow the user can leave.
    data object NavigateBack : SettingsEffect
}
