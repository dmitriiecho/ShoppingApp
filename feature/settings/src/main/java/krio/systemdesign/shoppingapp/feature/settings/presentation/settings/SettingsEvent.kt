package krio.systemdesign.shoppingapp.feature.settings.presentation.settings

import krio.systemdesign.shoppingapp.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.domain.model.ThemeMode

sealed interface SettingsEvent {

    data class OnThemeModeChange(val mode: ThemeMode) : SettingsEvent

    data class OnNetworkDelayChange(val delay: NetworkDelay) : SettingsEvent

    data object OnDeepLinksPageClick : SettingsEvent

    data object OnAddUnavailableProductClick : SettingsEvent

    data object OnAddNotEnoughStockProductClick : SettingsEvent

    data object OnAddPriceChangedProductClick : SettingsEvent

    data object OnAddPriceChangedNotEnoughStockProductClick : SettingsEvent

    // Во вкладке кнопки «Назад» нет. Событие нужно, когда экран встроен во флоу, из которого можно выйти.
    data object OnBackClick : SettingsEvent
}
