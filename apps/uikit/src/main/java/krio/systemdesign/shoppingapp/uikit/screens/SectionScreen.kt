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
import krio.systemdesign.shoppingapp.core.designsystem.components.buttons.NavigateBackIconButton
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.uikit.sections.designsystem.BarsSection
import krio.systemdesign.shoppingapp.uikit.sections.designsystem.ButtonsSection
import krio.systemdesign.shoppingapp.uikit.sections.designsystem.CardsSection
import krio.systemdesign.shoppingapp.uikit.sections.designsystem.DialogsSection
import krio.systemdesign.shoppingapp.uikit.sections.designsystem.IconsSection
import krio.systemdesign.shoppingapp.uikit.sections.designsystem.InputsSection
import krio.systemdesign.shoppingapp.uikit.sections.designsystem.LoadingSection
import krio.systemdesign.shoppingapp.uikit.sections.designsystem.NoticesSection
import krio.systemdesign.shoppingapp.uikit.sections.designsystem.ScreenStatesSection
import krio.systemdesign.shoppingapp.uikit.sections.designsystem.ThemeSection
import krio.systemdesign.shoppingapp.uikit.sections.feature.CartFeatureSection
import krio.systemdesign.shoppingapp.uikit.sections.feature.CatalogFeatureSection
import krio.systemdesign.shoppingapp.uikit.sections.feature.CheckoutFeatureSection
import krio.systemdesign.shoppingapp.uikit.sections.feature.PromoFeatureSection
import krio.systemdesign.shoppingapp.uikit.sections.shared.CartSection
import krio.systemdesign.shoppingapp.uikit.sections.shared.OrderSection
import krio.systemdesign.shoppingapp.uikit.sections.shared.ProductSection
import krio.systemdesign.shoppingapp.uikit.sections.shared.PromoSection

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
            UiKitSection.Icons -> IconsSection(innerPadding)
            UiKitSection.Buttons -> ButtonsSection(innerPadding)
            UiKitSection.Notices -> NoticesSection(innerPadding)
            UiKitSection.Cards -> CardsSection(innerPadding)
            UiKitSection.Inputs -> InputsSection(innerPadding)
            UiKitSection.Loading -> LoadingSection(innerPadding)
            UiKitSection.ScreenStates -> ScreenStatesSection(innerPadding)
            UiKitSection.Bars -> BarsSection(innerPadding)
            UiKitSection.Dialogs -> DialogsSection(innerPadding)
            UiKitSection.Product -> ProductSection(innerPadding)
            UiKitSection.Cart -> CartSection(innerPadding)
            UiKitSection.Order -> OrderSection(innerPadding)
            UiKitSection.Promo -> PromoSection(innerPadding)
            UiKitSection.CatalogFeature -> CatalogFeatureSection(innerPadding)
            UiKitSection.CartFeature -> CartFeatureSection(innerPadding)
            UiKitSection.PromoFeature -> PromoFeatureSection(innerPadding)
            UiKitSection.CheckoutFeature -> CheckoutFeatureSection(innerPadding)
        }
    }
}

// Bars is just an example section; the height fits all of it, so it can be seen without scrolling.
@Preview(name = "Light", heightDp = 900)
@Preview(name = "Dark", heightDp = 900, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SectionScreenPreview() {
    ShoppingAppTheme {
        SectionScreen(
            section = UiKitSection.Bars,
            darkTheme = isSystemInDarkTheme(),
            onToggleTheme = {},
            onBack = {},
        )
    }
}
