package krio.systemdesign.shoppingapp.feature.settings.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import krio.systemdesign.shoppingapp.core.config.DeepLinkConfig
import krio.systemdesign.shoppingapp.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.domain.usecase.ObserveThemeModeUseCase
import krio.systemdesign.shoppingapp.feature.settings.domain.usecase.AddNotEnoughStockProductToCartUseCase
import krio.systemdesign.shoppingapp.feature.settings.domain.usecase.AddPriceChangedNotEnoughStockProductToCartUseCase
import krio.systemdesign.shoppingapp.feature.settings.domain.usecase.AddPriceChangedProductToCartUseCase
import krio.systemdesign.shoppingapp.feature.settings.domain.usecase.AddUnavailableProductToCartUseCase
import krio.systemdesign.shoppingapp.feature.settings.domain.usecase.SetThemeModeUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeThemeMode: ObserveThemeModeUseCase,
    private val setThemeMode: SetThemeModeUseCase,
    private val addUnavailableProductToCart: AddUnavailableProductToCartUseCase,
    private val addNotEnoughStockProductToCart: AddNotEnoughStockProductToCartUseCase,
    private val addPriceChangedProductToCart: AddPriceChangedProductToCartUseCase,
    private val addPriceChangedNotEnoughStockProductToCart: AddPriceChangedNotEnoughStockProductToCartUseCase,
) : ViewModel() {

    private val _effects = Channel<SettingsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    val uiState: StateFlow<SettingsUiState> = observeThemeMode()
        .map { SettingsUiState(themeMode = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState(),
        )

    fun onEvent(event: SettingsEvent) {
        when (event) {
            // Тему применяет MainActivity: она следит за сохранённой настройкой, отдельно сообщать ей не нужно.
            is SettingsEvent.OnThemeModeChange -> changeThemeMode(event.mode)
            SettingsEvent.OnDeepLinksPageClick -> send(SettingsEffect.OpenUrl(DeepLinkConfig.TEST_PAGE_URI))
            SettingsEvent.OnAddUnavailableProductClick -> addUnavailableProduct()
            SettingsEvent.OnAddNotEnoughStockProductClick -> addNotEnoughStockProduct()
            SettingsEvent.OnAddPriceChangedProductClick -> addPriceChangedProduct()
            SettingsEvent.OnAddPriceChangedNotEnoughStockProductClick -> addPriceChangedNotEnoughStockProduct()
            SettingsEvent.OnBackClick -> send(SettingsEffect.NavigateBack)
        }
    }

    private fun changeThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            setThemeMode(mode).onFailure { send(SettingsEffect.ShowThemeSaveError) }
        }
    }

    private fun addUnavailableProduct() {
        viewModelScope.launch {
            addUnavailableProductToCart()
                .onSuccess { send(SettingsEffect.ShowUnavailableProductAdded) }
                .onFailure { send(SettingsEffect.ShowAddToCartError) }
        }
    }

    private fun addNotEnoughStockProduct() {
        viewModelScope.launch {
            addNotEnoughStockProductToCart()
                .onSuccess { send(SettingsEffect.ShowNotEnoughStockProductAdded) }
                .onFailure { send(SettingsEffect.ShowAddToCartError) }
        }
    }

    private fun addPriceChangedProduct() {
        viewModelScope.launch {
            addPriceChangedProductToCart()
                .onSuccess { send(SettingsEffect.ShowPriceChangedProductAdded) }
                .onFailure { send(SettingsEffect.ShowAddToCartError) }
        }
    }

    private fun addPriceChangedNotEnoughStockProduct() {
        viewModelScope.launch {
            addPriceChangedNotEnoughStockProductToCart()
                .onSuccess { send(SettingsEffect.ShowPriceChangedNotEnoughStockProductAdded) }
                .onFailure { send(SettingsEffect.ShowAddToCartError) }
        }
    }

    private fun send(effect: SettingsEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
