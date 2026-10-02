package krio.systemdesign.shoppingapp.uikit

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.ui.components.NavigateBackIconButton
import krio.systemdesign.shoppingapp.uikit.components.ThemeToggleButton
import krio.systemdesign.shoppingapp.uikit.sections.BarsSection
import krio.systemdesign.shoppingapp.uikit.sections.ButtonsSection
import krio.systemdesign.shoppingapp.uikit.sections.CardsSection
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
            UiKitSection.Inputs -> InputsSection(innerPadding)
            UiKitSection.Loading -> LoadingSection(innerPadding)
            UiKitSection.ScreenStates -> ScreenStatesSection(innerPadding)
            UiKitSection.Bars -> BarsSection(innerPadding)
        }
    }
}
