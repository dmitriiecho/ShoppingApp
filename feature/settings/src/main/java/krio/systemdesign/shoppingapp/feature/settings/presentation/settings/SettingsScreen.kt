package krio.systemdesign.shoppingapp.feature.settings.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.ui.components.buttons.SingleChoiceButtons
import krio.systemdesign.shoppingapp.core.ui.components.cards.AppListItem
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.feature.settings.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SettingsEffect.OpenUrl -> {
                    // Адрес открывается в браузере как в отдельном приложении. Если браузера нет, openUri бросает исключение.
                    try {
                        uriHandler.openUri(effect.url)
                    } catch (e: IllegalArgumentException) {
                        launch { snackbarHostState.showSnackbar(resources.getString(R.string.settings_no_browser)) }
                    }
                }
                SettingsEffect.ShowThemeSaveError -> {
                    launch { snackbarHostState.showSnackbar(resources.getString(R.string.settings_theme_save_error)) }
                }
                SettingsEffect.ShowNetworkDelaySaveError -> {
                    launch { snackbarHostState.showSnackbar(resources.getString(R.string.settings_network_delay_save_error)) }
                }
                SettingsEffect.ShowUnavailableProductAdded -> {
                    launch { snackbarHostState.showSnackbar(resources.getString(R.string.settings_unavailable_product_added)) }
                }
                SettingsEffect.ShowNotEnoughStockProductAdded -> {
                    launch { snackbarHostState.showSnackbar(resources.getString(R.string.settings_not_enough_stock_product_added)) }
                }
                SettingsEffect.ShowPriceChangedProductAdded -> {
                    launch { snackbarHostState.showSnackbar(resources.getString(R.string.settings_price_changed_product_added)) }
                }
                SettingsEffect.ShowPriceChangedNotEnoughStockProductAdded -> {
                    launch {
                        snackbarHostState.showSnackbar(resources.getString(R.string.settings_price_changed_not_enough_stock_product_added))
                    }
                }
                SettingsEffect.ShowAddToCartError -> {
                    launch { snackbarHostState.showSnackbar(resources.getString(R.string.settings_add_to_cart_error)) }
                }
                SettingsEffect.NavigateBack -> onBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        // Пока настройки не прочитаны (доли секунды), ничего не показываем:
        // иначе на миг были бы отмечены значения по умолчанию, а потом выбранные.
        val themeMode = uiState.themeMode ?: return@Scaffold
        val networkDelay = uiState.networkDelay ?: return@Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            ThemeModeItem(
                selected = themeMode,
                onSelect = { viewModel.onEvent(SettingsEvent.OnThemeModeChange(it)) },
            )
            // Ниже — инструменты для тестирования, линия отделяет их от настройки темы.
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            NetworkDelayItem(
                selected = networkDelay,
                onSelect = { viewModel.onEvent(SettingsEvent.OnNetworkDelayChange(it)) },
            )
            // Задержка — отдельный блок: она меняет все запросы, а пункты ниже — действия с корзиной и ссылками.
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            AppListItem(
                icon = AppIcons.Link,
                title = stringResource(R.string.settings_deep_links_page),
                description = stringResource(R.string.settings_deep_links_page_description),
                onClick = { viewModel.onEvent(SettingsEvent.OnDeepLinksPageClick) },
                trailing = { Icon(AppIcons.OpenInNew, contentDescription = null) },
            )
            // Значки трёх пунктов те же, что у плашек в корзине, которые эти товары вызывают.
            AppListItem(
                icon = AppIcons.OutOfStock,
                title = stringResource(R.string.settings_add_unavailable_product),
                description = stringResource(R.string.settings_add_unavailable_product_description),
                onClick = { viewModel.onEvent(SettingsEvent.OnAddUnavailableProductClick) },
            )
            AppListItem(
                icon = AppIcons.NotEnoughStock,
                title = stringResource(R.string.settings_add_not_enough_stock_product),
                description = stringResource(R.string.settings_add_not_enough_stock_product_description),
                onClick = { viewModel.onEvent(SettingsEvent.OnAddNotEnoughStockProductClick) },
            )
            AppListItem(
                icon = AppIcons.PriceChanged,
                title = stringResource(R.string.settings_add_price_changed_product),
                description = stringResource(R.string.settings_add_price_changed_product_description),
                onClick = { viewModel.onEvent(SettingsEvent.OnAddPriceChangedProductClick) },
            )
            AppListItem(
                // Своей плашки у сочетания проблем в корзине нет, поэтому иконка общая: «с товаром несколько проблем».
                icon = AppIcons.Warning,
                title = stringResource(R.string.settings_add_price_changed_not_enough_stock_product),
                description = stringResource(R.string.settings_add_price_changed_not_enough_stock_product_description),
                onClick = { viewModel.onEvent(SettingsEvent.OnAddPriceChangedNotEnoughStockProductClick) },
            )
        }
    }
}

// Тема приложения: как в системе, светлая или тёмная. Строка такая же, как остальные пункты:
// под заголовком выбранная тема словами, а справа переключатель из трёх иконок — тема меняется одним нажатием.
@Composable
private fun ThemeModeItem(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppListItem(
        icon = AppIcons.Contrast,
        title = stringResource(R.string.settings_theme),
        description = stringResource(selected.titleRes),
        modifier = modifier,
        trailing = {
            ChoiceButtons(options = ThemeMode.entries, selected = selected, onSelect = onSelect) { mode ->
                Icon(mode.icon, contentDescription = stringResource(mode.titleRes))
            }
        },
    )
}

// Задержка перед каждым запросом к серверу. Устроена как строка темы: под заголовком выбранное значение словами,
// справа переключатель, где значение меняется одним нажатием.
@Composable
private fun NetworkDelayItem(
    selected: NetworkDelay,
    onSelect: (NetworkDelay) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppListItem(
        icon = AppIcons.HourglassEmpty,
        title = stringResource(R.string.settings_network_delay),
        description = stringResource(selected.descriptionRes),
        modifier = modifier,
        trailing = {
            ChoiceButtons(options = NetworkDelay.entries, selected = selected, onSelect = onSelect) { delay ->
                Text(delay.duration.inWholeSeconds.toString())
            }
        },
    )
}

// Переключатель справа в строке настройки. Ширина у всех одинаковая, чтобы переключатели соседних строк
// стояли ровно друг под другом.
@Composable
private fun <T> ChoiceButtons(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: @Composable (T) -> Unit,
) {
    SingleChoiceButtons(
        options = options,
        selected = selected,
        onSelect = onSelect,
        modifier = Modifier.width(ChoiceButtonsWidth),
        label = label,
    )
}

private val ChoiceButtonsWidth = 168.dp

private val NetworkDelay.descriptionRes: Int
    get() = when (this) {
        NetworkDelay.None -> R.string.settings_network_delay_none
        NetworkDelay.TwoSeconds -> R.string.settings_network_delay_two_seconds
        NetworkDelay.FourSeconds -> R.string.settings_network_delay_four_seconds
    }

private val ThemeMode.titleRes: Int
    get() = when (this) {
        ThemeMode.System -> R.string.settings_theme_system
        ThemeMode.Light -> R.string.settings_theme_light
        ThemeMode.Dark -> R.string.settings_theme_dark
    }

private val ThemeMode.icon: ImageVector
    get() = when (this) {
        ThemeMode.System -> AppIcons.SystemTheme
        ThemeMode.Light -> AppIcons.LightTheme
        ThemeMode.Dark -> AppIcons.DarkTheme
    }
