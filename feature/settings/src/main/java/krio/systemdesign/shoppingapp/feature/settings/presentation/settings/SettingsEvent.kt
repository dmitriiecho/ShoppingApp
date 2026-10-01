package krio.systemdesign.shoppingapp.feature.settings.presentation.settings

sealed interface SettingsEvent {

    data object OnDeepLinksPageClick : SettingsEvent

    // Во вкладке кнопки «Назад» нет. Событие нужно, когда экран встроен во флоу, из которого можно выйти.
    data object OnBackClick : SettingsEvent
}
