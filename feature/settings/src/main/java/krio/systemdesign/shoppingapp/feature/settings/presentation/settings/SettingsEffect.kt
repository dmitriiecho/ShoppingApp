package krio.systemdesign.shoppingapp.feature.settings.presentation.settings

sealed interface SettingsEffect {

    data class OpenUrl(val url: String) : SettingsEffect

    data object ShowThemeSaveError : SettingsEffect

    // Во вкладке не отправляется. Нужен, когда экран встроен во флоу, из которого можно выйти.
    data object NavigateBack : SettingsEffect
}
