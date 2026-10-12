package krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.composeutils.effects.ObserveEffects
import krio.systemdesign.shoppingapp.core.composeutils.text.UiText
import krio.systemdesign.shoppingapp.core.composeutils.viewmodel.injectedViewModel
import krio.systemdesign.shoppingapp.core.config.DeepLinkConfig
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.contentBarWidth
import krio.systemdesign.shoppingapp.core.designsystem.theme.contentWidth
import krio.systemdesign.shoppingapp.crossfeature.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.crossfeature.domain.model.ThemeMode
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings.components.NetworkDelayItem
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings.components.TestActions
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings.components.ThemeModeItem
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.Res
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_deep_links_page_url
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_no_browser
import krio.systemdesign.shoppingapp.feature.settings.impl.resources.settings_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = injectedViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val deepLinksPageUrl = stringResource(Res.string.settings_deep_links_page_url, DeepLinkConfig.TEST_PAGES_URL)

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            SettingsEffect.OpenDeepLinksPage -> try {
                uriHandler.openUri(deepLinksPageUrl)
            } catch (_: IllegalArgumentException) {
                // No browser to open the URL in.
                showSnackbar(snackbarHostState, UiText.Resource(Res.string.settings_no_browser))
            }
            is SettingsEffect.ShowSnackBar -> showSnackbar(snackbarHostState, effect.message)
            SettingsEffect.NavigateBack -> navigate { onBack() }
        }
    }

    SettingsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    uiState: SettingsUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) },
                modifier = Modifier.contentBarWidth(),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        when (val values = uiState.values) {
            // Nothing for the split second of reading, or defaults would flash before the saved values.
            SettingsUiState.Values.Loading -> Unit
            is SettingsUiState.Values.Loaded -> SettingsContent(
                values = values,
                onEvent = onEvent,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )
        }
    }
}

@Composable
private fun SettingsContent(
    values: SettingsUiState.Values.Loaded,
    onEvent: (SettingsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .contentWidth(),
    ) {
        ThemeModeItem(
            selected = values.themeMode,
            onSelect = { onEvent(SettingsEvent.OnThemeModeChange(it)) },
        )
        // The theme is a real setting; everything below is a testing tool.
        SectionDivider()
        NetworkDelayItem(
            selected = values.networkDelay,
            onSelect = { onEvent(SettingsEvent.OnNetworkDelayChange(it)) },
        )
        // The delay affects every request, while the actions below are one-off.
        SectionDivider()
        TestActions(
            onDeepLinksPageClick = { onEvent(SettingsEvent.OnDeepLinksPageClick) },
            onAddUnavailableProductClick = { onEvent(SettingsEvent.OnAddUnavailableProductClick) },
            onAddNotEnoughStockProductClick = { onEvent(SettingsEvent.OnAddNotEnoughStockProductClick) },
            onAddPriceChangedProductClick = { onEvent(SettingsEvent.OnAddPriceChangedProductClick) },
            onAddPriceChangedNotEnoughStockProductClick = {
                onEvent(SettingsEvent.OnAddPriceChangedNotEnoughStockProductClick)
            },
        )
    }
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenPreview() {
    ShoppingAppTheme {
        SettingsScreen(
            uiState = SettingsUiState(
                values = SettingsUiState.Values.Loaded(themeMode = ThemeMode.System, networkDelay = NetworkDelay.None),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
        )
    }
}
