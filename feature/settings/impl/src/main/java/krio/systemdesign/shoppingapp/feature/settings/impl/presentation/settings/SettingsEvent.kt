package krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings

import krio.systemdesign.shoppingapp.shared.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.shared.domain.model.ThemeMode

sealed interface SettingsEvent {

    data class OnThemeModeChange(val mode: ThemeMode) : SettingsEvent

    data class OnNetworkDelayChange(val delay: NetworkDelay) : SettingsEvent

    data object OnDeepLinksPageClick : SettingsEvent

    data object OnAddUnavailableProductClick : SettingsEvent

    data object OnAddNotEnoughStockProductClick : SettingsEvent

    data object OnAddPriceChangedProductClick : SettingsEvent

    data object OnAddPriceChangedNotEnoughStockProductClick : SettingsEvent

    // A tab has no Back button. Needed when the screen is embedded in a flow the user can leave.
    data object OnBackClick : SettingsEvent
}
