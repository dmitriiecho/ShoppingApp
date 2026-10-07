package krio.systemdesign.shoppingapp.uikit.screens

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import krio.systemdesign.shoppingapp.core.designsystem.icons.AppIcons
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.DarkMode
import krio.systemdesign.shoppingapp.core.designsystem.icons.symbols.LightMode
import krio.systemdesign.shoppingapp.core.designsystem.theme.ShoppingAppTheme
import krio.systemdesign.shoppingapp.uikit.R

// Switches the UI kit between light and dark. It shows the theme it switches to, with the settings screen's icons.
@Composable
fun ThemeToggleButton(
    darkTheme: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onToggle, modifier = modifier) {
        if (darkTheme) {
            Icon(AppIcons.LightMode, contentDescription = stringResource(R.string.uikit_switch_to_light_theme))
        } else {
            Icon(AppIcons.DarkMode, contentDescription = stringResource(R.string.uikit_switch_to_dark_theme))
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ThemeToggleButtonPreview() {
    ShoppingAppTheme {
        // Same tint as in the TopAppBar actions.
        Surface(contentColor = MaterialTheme.colorScheme.onSurfaceVariant) {
            ThemeToggleButton(
                darkTheme = isSystemInDarkTheme(),
                onToggle = {},
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
