package krio.systemdesign.shoppingapp.uikit.screens

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import krio.systemdesign.shoppingapp.core.ui.components.buttons.NavigateBackIconButton
import krio.systemdesign.shoppingapp.core.ui.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.uikit.sections.BarsSection
import krio.systemdesign.shoppingapp.uikit.sections.ButtonsSection
import krio.systemdesign.shoppingapp.uikit.sections.CardsSection
import krio.systemdesign.shoppingapp.uikit.sections.DialogsSection
import krio.systemdesign.shoppingapp.uikit.sections.ImagesSection
import krio.systemdesign.shoppingapp.uikit.sections.InputsSection
import krio.systemdesign.shoppingapp.uikit.sections.LoadingSection
import krio.systemdesign.shoppingapp.uikit.sections.NoticesSection
import krio.systemdesign.shoppingapp.uikit.sections.ScreenStatesSection
import krio.systemdesign.shoppingapp.uikit.sections.ThemeSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionScreen(
    section: UiKitSection,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(section.titleRes)) },
                navigationIcon = {
                    NavigateBackIconButton(onClick = onBack)
                },
                actions = {
                    ThemeToggleButton(darkTheme = darkTheme, onToggle = onToggleTheme)
                },
            )
        },
    ) { innerPadding ->
        when (section) {
            UiKitSection.Theme -> ThemeSection(innerPadding)
            UiKitSection.Buttons -> ButtonsSection(innerPadding)
            UiKitSection.Notices -> NoticesSection(innerPadding)
            UiKitSection.Cards -> CardsSection(innerPadding)
            UiKitSection.Images -> ImagesSection(innerPadding)
            UiKitSection.Inputs -> InputsSection(innerPadding)
            UiKitSection.Loading -> LoadingSection(innerPadding)
            UiKitSection.ScreenStates -> ScreenStatesSection(innerPadding)
            UiKitSection.Bars -> BarsSection(innerPadding)
            UiKitSection.Dialogs -> DialogsSection(innerPadding)
        }
    }
}

// Tall enough for the whole Buttons section, so it can be seen without scrolling.
@Preview(name = "Light", heightDp = 1700)
@Preview(name = "Dark", heightDp = 1700, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SectionScreenPreview() {
    ShoppingAppTheme {
        SectionScreen(
            section = UiKitSection.Buttons,
            darkTheme = isSystemInDarkTheme(),
            onToggleTheme = {},
            onBack = {},
        )
    }
}
