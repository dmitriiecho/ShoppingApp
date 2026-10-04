package krio.systemdesign.shoppingapp.uikit.screens

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
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.ui.components.cards.AppListItem
import krio.systemdesign.shoppingapp.uikit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionListScreen(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onOpenSection: (UiKitSection) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.uikit_title)) },
                actions = {
                    ThemeToggleButton(darkTheme = darkTheme, onToggle = onToggleTheme)
                },
            )
        },
    ) { innerPadding ->
        // Отступом, а не contentPadding: иначе пункты прокручиваются под системными кнопками.
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            items(UiKitSection.entries) { section ->
                AppListItem(
                    icon = section.icon,
                    title = stringResource(section.titleRes),
                    description = stringResource(section.descriptionRes),
                    onClick = { onOpenSection(section) },
                )
            }
        }
    }
}
