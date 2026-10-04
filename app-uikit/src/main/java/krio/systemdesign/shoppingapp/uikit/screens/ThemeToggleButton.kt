package krio.systemdesign.shoppingapp.uikit.screens

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import krio.systemdesign.shoppingapp.core.ui.icons.AppIcons
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.DarkMode
import krio.systemdesign.shoppingapp.core.ui.icons.symbols.LightMode
import krio.systemdesign.shoppingapp.uikit.R

// Switches the catalog between light and dark. It shows the theme it switches to, with the settings screen's icons.
@Composable
fun ThemeToggleButton(
    darkTheme: Boolean,
    onToggle: () -> Unit,
) {
    IconButton(onClick = onToggle) {
        if (darkTheme) {
            Icon(AppIcons.LightMode, contentDescription = stringResource(R.string.uikit_switch_to_light_theme))
        } else {
            Icon(AppIcons.DarkMode, contentDescription = stringResource(R.string.uikit_switch_to_dark_theme))
        }
    }
}
