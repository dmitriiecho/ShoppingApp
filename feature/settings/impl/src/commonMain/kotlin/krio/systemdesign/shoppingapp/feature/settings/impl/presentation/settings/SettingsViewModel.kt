package krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.ContributesIntoMap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import krio.systemdesign.shoppingapp.core.composeutils.text.UiText
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.ViewModelKey
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.ViewModelScope
import krio.systemdesign.shoppingapp.crossfeature.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.crossfeature.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.crossfeature.domain.usecase.ObserveThemeModeUseCase
import krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase.AddNotEnoughStockProductToCartUseCase
import krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase.AddPriceChangedNotEnoughStockProductToCartUseCase
import krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase.AddPriceChangedProductToCartUseCase
import krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase.AddUnavailableProductToCartUseCase
import krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase.ObserveNetworkDelayUseCase
import krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase.SetNetworkDelayUseCase
import krio.systemdesign.shoppingapp.feature.settings.impl.domain.usecase.SetThemeModeUseCase
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.Res
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_add_to_cart_error
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_network_delay_save_error
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_not_enough_stock_product_added
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_price_changed_not_enough_stock_product_added
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_price_changed_product_added
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_theme_save_error
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_unavailable_product_added
import org.jetbrains.compose.resources.StringResource

@ViewModelKey
@ContributesIntoMap(ViewModelScope::class)
internal class SettingsViewModel(
    observeThemeMode: ObserveThemeModeUseCase,
    private val setThemeMode: SetThemeModeUseCase,
    observeNetworkDelay: ObserveNetworkDelayUseCase,
    private val setNetworkDelay: SetNetworkDelayUseCase,
    private val addUnavailableProductToCart: AddUnavailableProductToCartUseCase,
    private val addNotEnoughStockProductToCart: AddNotEnoughStockProductToCartUseCase,
    private val addPriceChangedProductToCart: AddPriceChangedProductToCartUseCase,
    private val addPriceChangedNotEnoughStockProductToCart: AddPriceChangedNotEnoughStockProductToCartUseCase,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        observeThemeMode(),
        observeNetworkDelay(),
    ) { themeMode, networkDelay ->
        SettingsUiState(values = SettingsUiState.Values.Loaded(themeMode, networkDelay))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(),
    )

    private val _effects = Channel<SettingsEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            // No need to notify anyone: MainViewModel observes the saved theme and applies it.
            is SettingsEvent.OnThemeModeChange -> changeThemeMode(event.mode)
            is SettingsEvent.OnNetworkDelayChange -> changeNetworkDelay(event.delay)
            SettingsEvent.OnDeepLinksPageClick -> send(SettingsEffect.OpenDeepLinksPage)
            SettingsEvent.OnAddUnavailableProductClick -> addTestProduct(
                add = { addUnavailableProductToCart() },
                addedMessage = Res.string.settings_unavailable_product_added,
            )
            SettingsEvent.OnAddNotEnoughStockProductClick -> addTestProduct(
                add = { addNotEnoughStockProductToCart() },
                addedMessage = Res.string.settings_not_enough_stock_product_added,
            )
            SettingsEvent.OnAddPriceChangedProductClick -> addTestProduct(
                add = { addPriceChangedProductToCart() },
                addedMessage = Res.string.settings_price_changed_product_added,
            )
            SettingsEvent.OnAddPriceChangedNotEnoughStockProductClick -> addTestProduct(
                add = { addPriceChangedNotEnoughStockProductToCart() },
                addedMessage = Res.string.settings_price_changed_not_enough_stock_product_added,
            )
            SettingsEvent.OnBackClick -> send(SettingsEffect.NavigateBack)
        }
    }

    private fun changeThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            setThemeMode(mode).onFailure { showSnackBar(Res.string.settings_theme_save_error) }
        }
    }

    private fun changeNetworkDelay(delay: NetworkDelay) {
        viewModelScope.launch {
            setNetworkDelay(delay).onFailure { showSnackBar(Res.string.settings_network_delay_save_error) }
        }
    }

    private fun addTestProduct(
        add: suspend () -> Result<Unit>,
        addedMessage: StringResource,
    ) {
        viewModelScope.launch {
            add()
                .onSuccess { showSnackBar(addedMessage) }
                .onFailure { showSnackBar(Res.string.settings_add_to_cart_error) }
        }
    }

    private fun showSnackBar(message: StringResource) {
        send(SettingsEffect.ShowSnackBar(UiText.Resource(message)))
    }

    private fun send(effect: SettingsEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
