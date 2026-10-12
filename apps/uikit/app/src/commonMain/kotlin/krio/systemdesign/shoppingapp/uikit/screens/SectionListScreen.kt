package krio.systemdesign.shoppingapp.uikit.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.NavigateBackIconButton
import krio.systemdesign.shoppingapp.core.designsystem.components.cards.AppListItem
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SectionListScreen(
    group: UiKitSection.Group,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onOpenSection: (UiKitSection) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(group.title)) },
                navigationIcon = {
                    NavigateBackIconButton(onClick = onBack)
                },
                actions = {
                    ThemeToggleButton(darkTheme = darkTheme, onToggle = onToggleTheme)
                },
            )
        },
    ) { innerPadding ->
        // padding, not contentPadding: otherwise items scroll under the system buttons.
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            items(UiKitSection.entries.filter { it.group == group }) { section ->
                AppListItem(
                    icon = section.icon,
                    title = stringResource(section.title),
                    description = stringResource(section.description),
                    onClick = { onOpenSection(section) },
                )
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Composable
private fun SectionListScreenPreview() {
    ShoppingAppTheme {
        SectionListScreen(
            group = UiKitSection.Group.Shared,
            darkTheme = isSystemInDarkTheme(),
            onToggleTheme = {},
            onOpenSection = {},
            onBack = {},
        )
    }
}
