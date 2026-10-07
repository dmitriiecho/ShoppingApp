package krio.systemdesign.shoppingapp.uikit.sections.designsystem

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.components.bars.AppNavigationBarItem
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.HomeFilled
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.NotificationsFilled
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.SettingsFilled
import krio.systemdesign.shoppingapp.uikit.R
import krio.systemdesign.shoppingapp.uikit.samples.SampleList
import krio.systemdesign.shoppingapp.uikit.samples.SampleVariant
import krio.systemdesign.shoppingapp.uikit.samples.sampleGroup

@Composable
internal fun BarsSection(innerPadding: PaddingValues) {
    SampleList(innerPadding) {
        // Bars span the whole frame, as they span the screen in the app.
        sampleGroup("NavigationBar · AppNavigationBarItem") {
            SampleVariant(caption = stringResource(R.string.uikit_variant_badge), contentPadding = 0.dp) {
                SampleNavigationBar(badgeCount = 3)
            }
            SampleVariant(caption = stringResource(R.string.uikit_variant_badge_overflow), contentPadding = 0.dp) {
                SampleNavigationBar(badgeCount = 120)
            }
        }
    }
}

// A bottom bar with three tabs, the middle one with a badge; the tabs switch.
@Composable
private fun SampleNavigationBar(badgeCount: Int) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    NavigationBar {
        AppNavigationBarItem(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            icon = AppIcons.HomeFilled,
            label = stringResource(R.string.uikit_sample_tab_home),
        )
        AppNavigationBarItem(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            icon = AppIcons.NotificationsFilled,
            label = stringResource(R.string.uikit_sample_tab_notifications),
            badgeCount = badgeCount,
        )
        AppNavigationBarItem(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            icon = AppIcons.SettingsFilled,
            label = stringResource(R.string.uikit_sample_tab_settings),
        )
    }
}
