package krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings

import android.content.res.Configuration
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
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import krio.systemdesign.shoppingapp.core.composeutils.effects.ObserveEffects
import krio.systemdesign.shoppingapp.core.composeutils.text.asString
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.feature.settings.impl.R
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings.components.NetworkDelayItem
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings.components.TestActions
import krio.systemdesign.shoppingapp.feature.settings.impl.presentation.settings.components.ThemeModeItem
import krio.systemdesign.shoppingapp.shared.domain.model.NetworkDelay
import krio.systemdesign.shoppingapp.shared.domain.model.ThemeMode

@Composable
internal fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    ObserveEffects(viewModel.effects) { effect ->
        when (effect) {
            is SettingsEffect.OpenUrl -> try {
                uriHandler.openUri(effect.url.asString(resources))
            } catch (_: IllegalArgumentException) {
                // No browser to open the URL in.
                showSnackbar(snackbarHostState, resources.getString(R.string.settings_no_browser))
            }
            is SettingsEffect.ShowSnackBar -> showSnackbar(snackbarHostState, effect.message.asString(resources))
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
                title = { Text(stringResource(R.string.settings_title)) },
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
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
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
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
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
