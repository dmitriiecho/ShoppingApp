package krio.systemdesign.shoppingapp.feature.settings.presentation.settings

sealed interface SettingsEffect {

    data class OpenUrl(val url: String) : SettingsEffect

    data object ShowThemeSaveError : SettingsEffect

    data object ShowNetworkDelaySaveError : SettingsEffect

    data object ShowUnavailableProductAdded : SettingsEffect

    data object ShowNotEnoughStockProductAdded : SettingsEffect

    data object ShowPriceChangedProductAdded : SettingsEffect

    data object ShowPriceChangedNotEnoughStockProductAdded : SettingsEffect

    data object ShowAddToCartError : SettingsEffect

    // Во вкладке не отправляется. Нужен, когда экран встроен во флоу, из которого можно выйти.
    data object NavigateBack : SettingsEffect
}
