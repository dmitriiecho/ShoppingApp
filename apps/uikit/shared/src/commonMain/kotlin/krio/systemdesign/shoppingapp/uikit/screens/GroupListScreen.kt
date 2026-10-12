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
import krio.systemdesign.shoppingapp.core.designsystem.components.cards.AppListItem
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.core.designsystem.theme.contentBarWidth
import krio.systemdesign.shoppingapp.core.designsystem.theme.contentWidth
import krio.systemdesign.shoppingapp.uikit.resources.Res
import krio.systemdesign.shoppingapp.uikit.resources.uikit_title
import org.jetbrains.compose.resources.stringResource

// The first screen: the three groups of sections, by the module the components live in.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GroupListScreen(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onOpenGroup: (UiKitSection.Group) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.uikit_title)) },
                modifier = Modifier.contentBarWidth(),
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
            items(UiKitSection.Group.entries) { group ->
                AppListItem(
                    icon = group.icon,
                    title = stringResource(group.title),
                    description = stringResource(group.description),
                    modifier = Modifier.contentWidth(),
                    onClick = { onOpenGroup(group) },
                )
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Composable
private fun GroupListScreenPreview() {
    ShoppingAppTheme {
        GroupListScreen(
            darkTheme = isSystemInDarkTheme(),
            onToggleTheme = {},
            onOpenGroup = {},
        )
    }
}
