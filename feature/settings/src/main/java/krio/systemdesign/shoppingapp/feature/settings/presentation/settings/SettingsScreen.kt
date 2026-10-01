package krio.systemdesign.shoppingapp.feature.settings.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import krio.systemdesign.shoppingapp.feature.settings.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_deep_links_page)) },
                supportingContent = { Text(stringResource(R.string.settings_deep_links_page_description)) },
                leadingContent = { Icon(Icons.Default.Link, contentDescription = null) },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null) },
                modifier = Modifier.clickable { viewModel.onEvent(SettingsEvent.OnDeepLinksPageClick) },
            )
        }
    }
}
